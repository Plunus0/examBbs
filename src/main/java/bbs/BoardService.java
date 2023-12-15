package bbs;

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

    //삽입
    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    //조회
    public List<Board> getAllBoard() {
        return boardRepository.findAll();
    }
}
