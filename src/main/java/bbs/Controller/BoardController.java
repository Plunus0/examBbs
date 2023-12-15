package bbs.Controller;

import bbs.Entity.Board;
import bbs.Service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
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

    /*초기화*/
    @GetMapping("/boards")
    public String showAllPosts(Model model) {
        List<Board> boards = boardService.getAllBoard();
        model.addAttribute("boards", boards);
        return "boards";
    }
    @PostMapping
    public Board saveBoard(@RequestBody Board board) {
        return boardService.saveBoard(board);
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
    }


}
