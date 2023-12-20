package com.exam.examBbs.service;

import com.exam.examBbs.domain.Board;
import com.exam.examBbs.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    //전체
/*    public List<Board> getAllBoards() { return boardRepository.findAll(); }*/
/*    public Page<Board> getPaginatedBoard(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }*/

    public Page<Board> getPaginatedBoard(Pageable pageable) {
        Sort sort = Sort.by(Sort.Order.desc("BoardId"));
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return boardRepository.findAll(pageable);
    }

    /*상세*/
    public Board getBoardById(String boardId) {
        return boardRepository.findById(boardId).orElse(null);
    }

    //삽입
    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    /*수정*/
    public Board updateBoard(String boardId, Board updatedBoard) {
        Board board = boardRepository.findById(boardId).orElse(null);
        if (board != null) {
            board.setTitle(updatedBoard.getTitle());
            board.setTitle(updatedBoard.getContent());
            return boardRepository.save(board);
        }
        return null;
    }

    /*삭제*/
    public void deleteBoard(String boardId) {
        boardRepository.deleteById(boardId);
    }
}
