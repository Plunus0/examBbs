package com.exam.examBbs.service;

import com.exam.examBbs.domain.Board;
import com.exam.examBbs.domain.dto.BoardInsertRequest;
import com.exam.examBbs.domain.dto.BoardUpdateRequest;
import com.exam.examBbs.domain.dto.ResponseAllBoard;
import com.exam.examBbs.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;

    public List<ResponseAllBoard> getPaginatedBoard(/*Pageable pageable*/) {
//        Sort sort = Sort.by(Sort.Order.desc("BoardId"));
//        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        List<Board> boards = boardRepository.findAll();

        return boards.stream()
                .map(member -> modelMapper.map(member, ResponseAllBoard.class))
                .collect(Collectors.toList());
    }

    /*상세*/
    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId).orElse(null);
    }

    //생성
    public Board saveBoard(BoardInsertRequest dto) {
        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        return boardRepository.save(board);
    }

    /*수정*/
    public Board updateBoard(Long boardId, BoardUpdateRequest dto) {
        //인증절차(관리자, 작성자, 임시작성자)

        //자료 확인

        //수정
        Board board = boardRepository.findById(boardId).orElse(null);
        if (board != null) {
            Board.builder()
                    .boardId(boardId)
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .build();

            return boardRepository.save(board);
        }
        return null;
    }

    /*삭제*/
    public void deleteBoard(Long boardId) {
        //인증절차(관리자, 작성자, 임시작성자)
        boardRepository.deleteById(boardId);
    }
}
