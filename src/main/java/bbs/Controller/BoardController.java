package bbs.Controller;

import bbs.Entity.Board;
import bbs.Service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<Board>> getAllBoards() {
        List<Board> boards = boardService.getAllBoards();
        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable Long id) {
        Board board = boardService.getBoardById(id);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable Long id, @RequestBody Board updatedBoard) {
        Board board = boardService.updateBoard(id, updatedBoard);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }





/*
    @PostMapping
    public Board saveBoard(@RequestBody Board board) {
        return boardService.saveBoard(board);
    }

    @GetMapping("/boards")
    public String showAllPosts(Model model) {
        List<Board> boards = boardService.getAllBoard();
        model.addAttribute("boards", boards);
        return "boards";
    }

    @GetMapping("/{boardId}")
    public Board getBoardById(@PathVariable Long boardId) {
        return boardService.getBoardById(boardId);
    }

    @PutMapping("/{boardId}")
    public Board updateBoardTitle(@PathVariable Long boardId, @RequestParam String newTitle) {
        return boardService.updateBoardTitle(boardId, newTitle);
    }

    @DeleteMapping("/{boardId}")
    public void deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
    }*/

}
