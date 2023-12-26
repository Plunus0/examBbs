package com.exam.examBbs.controller;

import com.exam.examBbs.domain.Board;
import com.exam.examBbs.domain.Member;
import com.exam.examBbs.domain.dto.BoardDetailDTO;
import com.exam.examBbs.domain.dto.BoardListDTO;
import com.exam.examBbs.domain.dto.BoardSaveRequest;
import com.exam.examBbs.domain.dto.BoardUpdateRequest;
import com.exam.examBbs.exception.AppException;
import com.exam.examBbs.exception.ErrorCode;
import com.exam.examBbs.repository.BoardRepository;
import com.exam.examBbs.repository.MemberRepository;
import com.exam.examBbs.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bbs")
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<Page<BoardListDTO>> getBoards(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("boardId").descending());
        Page<BoardListDTO> boardList = boardService.getBoardList(pageable, searchType, searchText);
        return ResponseEntity.ok().body(boardList);
    }

    //게시글 작성(토큰 검증 주석처리)
    @PostMapping("/write")
    public ResponseEntity<BoardDetailDTO> saveBoard(@RequestBody BoardSaveRequest dto/*,
                                                    @RequestHeader("Authorization") String token*/) {
        BoardDetailDTO boardDetail = boardService.saveBoard(dto/*, token*/);
        return new ResponseEntity<>(boardDetail, HttpStatus.CREATED);
    }

    //게시글 상세조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailDTO> getBoardById(@PathVariable Long boardId) {
        return new ResponseEntity<>(boardService.getBoardById(boardId), HttpStatus.OK);
    }

    //게시글 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardDetailDTO> updateBoard(@PathVariable Long boardId,
                                                      @RequestBody BoardUpdateRequest request/*,
                                                      @RequestHeader(value = "Authorization", required = false) String token*/) {
        BoardDetailDTO updatedBoard = boardService.updateBoard(boardId, request/*, token*/);
        return ResponseEntity.ok(updatedBoard);
    }

    //게시글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //api문서 작성 스웨거설치
}
