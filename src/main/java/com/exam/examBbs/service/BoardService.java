package com.exam.examBbs.service;

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
import com.exam.examBbs.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

//    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    private BoardListDTO convertToBoardListDTO(Board board) {
        return BoardListDTO.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .author(board.getAuthor().getName())
                .updateDate(board.getUpdateDate())
                .build();

    }
    public Page<BoardListDTO> getBoardList(Pageable pageable, String searchType, String searchText) {

        Specification<Board> spec = Specification.where(null);

        if ("title".equals(searchType)) {
            spec = spec.and(BoardSpecifications.titleContains(searchText));
        } else if ("content".equals(searchType)) {
            spec = spec.and(BoardSpecifications.contentContains(searchText));
        } else if ("author".equals(searchType)) {
            spec = spec.and(BoardSpecifications.authorNameContains(searchText));
        }

        return boardRepository.findAll(spec, pageable).map(this::convertToBoardListDTO);
    }

    //생성(토큰 검증 주석처리)
    public BoardDetailDTO saveBoard(BoardSaveRequest request/*, String token*/) {

//        Long userId = JwtUtil.getUserIdFromToken(token, secretKey); // JWT 토큰에서 사용자 ID 추출
        Long userId = 3L; // 하드코딩된 사용자 ID
        Member author = memberRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Member not found with id: " + userId));

        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .regDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        Board savedBoard = boardRepository.save(board);

        return new BoardDetailDTO(
                savedBoard.getBoardId(),
                savedBoard.getTitle(),
                savedBoard.getContent(),
                savedBoard.getAuthor().getName(),
                savedBoard.getRegDate(),
                savedBoard.getUpdateDate()
        );
    }

    /*상세*/
    public BoardDetailDTO getBoardById(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."));

        // 조회수 증가
        Board updatedBoard = board.increaseViewCount();
        boardRepository.save(updatedBoard);

        // BoardDetailDTO 반환
        return new BoardDetailDTO(
                updatedBoard.getBoardId(),
                updatedBoard.getTitle(),
                updatedBoard.getContent(),
                updatedBoard.getAuthor().getName(),
                updatedBoard.getRegDate(),
                updatedBoard.getUpdateDate()
        );
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
