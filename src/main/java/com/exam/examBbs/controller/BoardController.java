package com.exam.examBbs.controller;

import com.exam.examBbs.domain.dto.*;
import com.exam.examBbs.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bbs")
public class BoardController {
    private final BoardService boardService;
    @Value("${jwt.secret}")
    private String secretKey;

    //게시글 전체 List 조회
    @GetMapping
    public ResponseEntity<Page<ResBoardList>> getActiveBoardList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("boardId").descending());
        Page<ResBoardList> boardList = boardService.getActiveBoardList(pageable, searchType, searchText);
        return ResponseEntity.ok().body(boardList);
    }

    //게시글 작성
    @PostMapping("/write")
    public ResponseEntity<ResBoardDetail> saveBoard(@RequestBody ReqBoardSave dto) {
        ResBoardDetail boardDetail = boardService.saveBoard(dto);
        return new ResponseEntity<>(boardDetail, HttpStatus.CREATED);
    }

    //게시글 상세조회
    @GetMapping("/{boardId}")
    public ResponseEntity<ResBoardDetail> getActiveBoardById(@PathVariable Long boardId) {
        return new ResponseEntity<>(boardService.getActiveBoardById(boardId), HttpStatus.OK);
    }

    //게시글 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<ResBoardDetail> updateBoard(@PathVariable Long boardId,
                                                      @RequestBody ReqBoardUpdate dto) {
        //클라이언트에서 넘어온 데이터를 절대 신뢰하지말것
        //dto로 넘어온 데이터 유효성 검증(어디서?),첨부파일 받아올 수 있는지?, 밀리초 확인하기
        ResBoardDetail updatedBoard = boardService.updateBoard(boardId, dto);
        return ResponseEntity.ok(updatedBoard);
    }

    //게시글 비활성화
    @PostMapping("/{boardId}")
    public ResponseEntity<String> deactivateBoard(@PathVariable Long boardId,
                                                  @RequestBody ReqBoardDeactivate dto) {
        boardService.deactivateBoard(boardId, dto);
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    //게시글 삭제
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //간접 비활성화된 게시글 전체 List 조회(관리자만 접근)
    @GetMapping("/deactive")
    public ResponseEntity<Page<ResBoardList>> getDeactiveBoardList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("boardId").descending());
        Page<ResBoardList> boardList = boardService.getDeactiveBoardList(pageable, searchType, searchText);
        return ResponseEntity.ok().body(boardList);
    }
    //간접 비활성화된 게시글 상세조회(관리자만 접근)
    @GetMapping("/deactive/{boardId}")
    public ResponseEntity<ResBoardDetail> getDeactiveBoardById(@PathVariable Long boardId) {
        return new ResponseEntity<>(boardService.getDeactiveBoardById(boardId), HttpStatus.OK);
    }
    //간접 비활성화된 게시글 활성화하기(관리자만 접근)
    @PostMapping("/deactive/{boardId}")
    public ResponseEntity<String> activateBoard(@PathVariable Long boardId) {
        boardService.activateBoard(boardId);
        return ResponseEntity.ok("게시글이 복구되었습니다.");
    }
}
