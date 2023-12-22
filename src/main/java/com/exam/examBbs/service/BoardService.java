package com.exam.examBbs.service;

import com.exam.examBbs.domain.Board;
import com.exam.examBbs.domain.Member;
import com.exam.examBbs.domain.dto.BoardInsertRequest;
import com.exam.examBbs.domain.dto.BoardListDTO;
import com.exam.examBbs.domain.dto.BoardUpdateRequest;
import com.exam.examBbs.exception.AppException;
import com.exam.examBbs.exception.ErrorCode;
import com.exam.examBbs.repository.BoardRepository;
import com.exam.examBbs.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

//    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    private BoardListDTO convertToBoardListDTO(Board board) {
        System.out.println("method convertToBoardListDTO");

        return BoardListDTO.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .authorName(board.getAuthor().getName())
                .updateDate(board.getUpdateDate())
                .build();

    }
//    public Page<BoardListDTO> getBoardList(Pageable pageable,String searchType, String searchText) {
//        Specification<Board> spec = Specification.where(null);
//        System.out.println("SERVICE START");
//        try {
//            logger.info("Fetching data, Page number: {}, Page size: {}", pageable.getPageNumber(), pageable.getPageSize());
//            System.out.println("TRY START");
//            // Specification 조건 추가
//            // ...
//            if ("title".equals(searchType)) {
//                System.out.println("title start");
//                spec = spec.and(BoardSpecifications.titleContains(searchText));
//            } else if ("content".equals(searchType)) {
//                System.out.println("content start");
//                spec = spec.and(BoardSpecifications.contentContains(searchText));
//            } else if ("author".equals(searchType)) {
//                System.out.println("author start");
//                spec = spec.and(BoardSpecifications.authorNameContains(searchText));
//            }
//
//        } catch (Exception e) {
//            logger.error("Error occurred while fetching data", e);
//            System.out.println("end");
//            throw e; // 또는 적절한 예외 처리
//        }
//
//        logger.info("Specification: {}", spec);
//
//        Page<BoardListDTO> result = boardRepository.findAll(spec, pageable).map(this::convertToBoardListDTO);
//
//        logger.info("Retrieved data, Number of elements: {}", result.getNumberOfElements());
//        System.out.println("SERVICE END");
//        return result;

//        Specification<Board> spec = Specification.where(null);
//
//        if ("title".equals(searchType)) {
//            spec = spec.and(BoardSpecifications.titleContains(searchText));
//        } else if ("content".equals(searchType)) {
//            spec = spec.and(BoardSpecifications.contentContains(searchText));
//        } else if ("author".equals(searchType)) {
//            spec = spec.and(BoardSpecifications.authorNameContains(searchText));
//        }
//
//        return boardRepository.findAll(spec, pageable).map(this::convertToBoardListDTO);
//    }






    /*상세*/
    public Board getBoardById(Long id) {
        System.out.println("id parameter = "+id);
        Optional<Board> board = boardRepository.findById(id);
        if (board.isPresent()) {
            return board.get();
        } else {
            throw new AppException(ErrorCode.NOT_FOUND, "해당 게시글을 찾을 수 없습니다.");
        }
    }

    //생성
    public Board saveBoard(BoardInsertRequest dto) {
        Member author = memberRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Member not found with id: " + dto.getAuthorId()));

        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(author)
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
