package com.exam.examBbs.controller;

import com.exam.examBbs.domain.dto.*;
import com.exam.examBbs.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    @Value("${jwt.secret}")
    private String secretKey;

    //게시글 전체 List 조회
    @GetMapping
    public ResponseEntity<Page<ResBoardList>> getBoards(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("boardId").descending());
        Page<ResBoardList> boardList = boardService.getBoardList(pageable, searchType, searchText);
        return ResponseEntity.ok().body(boardList);
    }

    //게시글 작성 토큰을 리퀘스트헤더로 지정하지 않아도 리퀘스트헤더로 토큰을 받는다? 그래선 안됐음
    @PostMapping("/write")
    public ResponseEntity<ResBoardDetail> saveBoard(@RequestBody ReqBoardSave dto,
                                                    @RequestHeader(value = "Authorization", required = false) String token) {
        ResBoardDetail boardDetail = boardService.saveBoard(dto, token);
        return new ResponseEntity<>(boardDetail, HttpStatus.CREATED);
    }

    //게시글 상세조회
    @GetMapping("/{boardId}")
    public ResponseEntity<ResBoardDetail> getBoardById(@PathVariable Long boardId) {
        return new ResponseEntity<>(boardService.getBoardById(boardId), HttpStatus.OK);
    }

    //게시글 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<ResBoardDetail> updateBoard(@PathVariable Long boardId,
                                                      @RequestBody ReqBoardUpdate dto,
                                                      @RequestHeader(value = "Authorization", required = false) String token) {
        ResBoardDetail updatedBoard = boardService.updateBoard(boardId, dto, token);
        return ResponseEntity.ok(updatedBoard);
    }

    //게시글 비활성화
    @PostMapping("/{boardId}")
    public ResponseEntity<String> deactivateBoard(@PathVariable Long boardId,
                                                  @RequestBody ReqBoardDeactivate dto,
                                                  @RequestHeader(value = "Authorization", required = false) String token) {
        boardService.deactivateBoard(boardId, dto, token);
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    //게시글 삭제
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //간접 비활성화된 게시글 전체 List 조회(관리자만 접근)

    //간접 비활성화된 게시글 상세조회(관리자만 접근)

    //간접 비활성화된 게시글 복원하기(관리자만 접근)
}
