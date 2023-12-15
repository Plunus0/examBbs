package bbs.Service;

import bbs.Entity.Board;
import bbs.Repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    //전체

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }
    /*상세*/

    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId).orElse(null);
    }
    //삽입
    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    /*수정*/
    public Board updateBoard(Long boardId, Board updatedBoard) {
        Board board = boardRepository.findById(boardId).orElse(null);
        if (board != null) {
            board.setTitle(updatedBoard.getTitle());
            board.setTitle(updatedBoard.getContent());
            return boardRepository.save(board);
        }
        return null;
    }

    /*삭제*/
    public void deleteBoard(Long boardId) {
        boardRepository.deleteById(boardId);
    }
}
