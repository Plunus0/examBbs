package com.exam.examBbs.Controller;

import com.exam.examBbs.Entity.Board;
import com.exam.examBbs.Service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/bbs/board")
public class BoardController {
    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping
    public ResponseEntity<Board> saveBoard(@RequestBody Board board) {
        Board saveBoard = boardService.saveBoard(board);
        return new ResponseEntity<>(saveBoard, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<Board>> getAllBoards(@RequestParam(defaultValue = "0") int page) {
        int pageSize = 5; // 원하는 페이지 크기
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Board> boards = boardService.getPaginatedBoard(pageable);
        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable String id) {
        Board board = boardService.getBoardById(id);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable String id, @RequestBody Board updatedBoard) {
        Board board = boardService.updateBoard(id, updatedBoard);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable String id) {
        boardService.deleteBoard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
