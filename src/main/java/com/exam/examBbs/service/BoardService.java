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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    @Value("${jwt.secret}")
    private String secretKey;
    private Long userId = 2L;
//    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    public Page<BoardListDTO> getBoardList(Pageable pageable, String searchType, String searchText) {
        //활성화된 게시글만 검색하는 spec 객체 생성
        Specification<Board> spec = BoardSpecifications.isActive();
        //if문으로 searchType와 searchText에 따른 검색메서드 실행
        if (searchText != null && !searchText.trim().isEmpty()) {
            if ("title".equals(searchType)) {
                spec = spec.and(BoardSpecifications.titleContains(searchText));
            } else if ("content".equals(searchType)) {
                spec = spec.and(BoardSpecifications.contentContains(searchText));
            } else if ("author".equals(searchType)) {
                spec = spec.and(BoardSpecifications.authorNameContains(searchText));
            }
        }
        //검색결과를 convertToBoardListDTO로 Mapping하여 반환
        return boardRepository.findAll(spec, pageable).map(this::convertToBoardListDTO);
    }

    //게시글 전체 조회 중 제공할 정보만 BoardListDTO에 Mapping하는 메서드
    private BoardListDTO convertToBoardListDTO(Board board) {
        return BoardListDTO.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .authorName(board.getAuthor() != null ? board.getAuthor().getName() : "비회원")
                .regDate(board.getRegDate())
                .updateDate(board.getUpdateDate())
                .build();
    }

    //생성(토큰 검증 주석처리)
    public BoardDetailDTO saveBoard(BoardSaveRequest request/*, String token*/) {
        Member author = null;
        //JWT 토큰에서 사용자 ID 추출
//        Long userId = JwtUtil.getUserIdFromToken(token, secretKey);
        // JWT에서 추출된 userId를 확인하고 없다면 비회원임을 확인, userId가 있으나 매칭되지 않을 경우(위변조, 에러 등) 예외처리
        if (userId != null) {
            author = memberRepository.findActiveById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED, "글을 작성할 수 있는 권한이 없습니다."));
        }
        //비회원이 비밀번호를 입력하지 않았다면 예외처리, 그렇지 않다면 게시글의 비밀번호로 입력
        String password;
        if(userId == null && request.getPassword() == null){
            throw new AppException(ErrorCode.INVALID_OPERATION, "비회원은 게시글 작성 비밀번호를 입력해야 합니다.");
        }else {
            password = request.getPassword();
        }

        //입력된 게시글 저장
        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author) //null이면 비회원
                .password(password) //회원이면 null
                .regDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        Board savedBoard = boardRepository.save(board);
        //작성한 게시글을 반환
        return new BoardDetailDTO(
                savedBoard.getBoardId(),
                savedBoard.getTitle(),
                savedBoard.getContent(),
                savedBoard.getAuthor() != null ? savedBoard.getAuthor().getName() : "비회원",
                savedBoard.getRegDate(),
                savedBoard.getUpdateDate()
        );
    }

    /*상세*/
    public BoardDetailDTO getBoardById(Long boardId) {
        //boardId를 확인하여 게시글을 가져오거나 없다면 예외처리
        Board board = boardRepository.findActiveById(boardId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."));

        //조회수 증가
        Board updatedBoard = board.increaseViewCount();
        boardRepository.save(updatedBoard);

        //요청한 게시글 반환
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
    public BoardDetailDTO updateBoard(Long boardId, BoardUpdateRequest dto/*, String token*/) {
        //boardId를 확인하여 게시글을 가져오거나 없다면 예외처리
                Board board = boardRepository.findActiveById(boardId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."));

/*        Long userId = null;
        if (token != null && !token.isEmpty()) {
            Long userId = JwtUtil.getUserIdFromToken(token, secretKey); // 토큰에서 사용자 ID 추출
        }*/
        //userId가 있을 경우 비교하여 작성자 혹은 관리자인지 확인 그렇지 않다면 예외처리
        boolean isOwner = false;
        if (userId != null) {
            Member member = memberRepository.findActiveById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "글을 수정할 수 있는 권한이 없습니다."));
            isOwner = board.getAuthor() != null && board.getAuthor().getMemberId().equals(userId) || member.getIsAdmin();
        }
        //작성자 또는 관리자가 아닌 경우 비밀번호 검증
        if (!isOwner) {
            if (dto.getPassword() == null || !dto.getPassword().equals(board.getPassword())) {
                throw new AppException(ErrorCode.UNAUTHORIZED, "비밀번호 입력 오류입니다.");
            }
        }

        //게시글 수정
        board.update(dto.getTitle(), dto.getContent());
        Board updatedBoard = boardRepository.save(board);

        return new BoardDetailDTO(
                updatedBoard.getBoardId(),
                updatedBoard.getTitle(),
                updatedBoard.getContent(),
                updatedBoard.getAuthor() != null ? updatedBoard.getAuthor().getName() : "비회원",
                updatedBoard.getRegDate(),
                updatedBoard.getUpdateDate()
        );
    }

    /*삭제*/
    public void deleteBoard(Long boardId) {
        //인증절차(관리자, 작성자, 임시작성자)
        boardRepository.deleteById(boardId);
    }
}
