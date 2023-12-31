package com.exam.examBbs.service;

import com.exam.examBbs.domain.Board;
import com.exam.examBbs.domain.MemberLogin;
import com.exam.examBbs.domain.dto.*;
import com.exam.examBbs.exception.AppException;
import com.exam.examBbs.exception.ErrorCode;
import com.exam.examBbs.repository.BoardRepository;
import com.exam.examBbs.repository.MemberInfoRepository;
import com.exam.examBbs.repository.MemberLoginRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberLoginRepository memberLoginRepository;
    private final MemberInfoRepository memberInfoRepository;
    @Value("${jwt.secret}")
    private String secretKey;
    MemberLogin author = null;
    String password = null;
    Long memberId = 1L;
    String auth = null;

    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    //게시글 전체 List조회 (검색 및 페이징처리 포함) *인증 필요없음
    public Page<ResBoardList> getActiveBoardList(Pageable pageable, String searchType, String searchText) {
        //활성화된 게시글만 저장하는 spec 객체 생성
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
    private ResBoardList convertToBoardListDTO(Board board) {
        return ResBoardList.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .authorName(board.getMemberId() != null ? board.getMemberId().getMemberInfo().getName() : "비회원")
                .regDate(board.getRegDate())
                .updateDate(board.getUpdateDate())
                .build();
    }

    //게시글 생성 *authentication객체로 인증정보를 가져와 사용자를 확인하고 이를 memberId에 저장 *인증 필요없음
    public ResBoardDetail saveBoard(ReqBoardSave dto) {
        //authentication객체에 SecurityContextHolder를 담아서 인증정보를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //authentication에서 memberId 추출
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            memberId = ((MemberDetails) authentication.getPrincipal()).getMemberId();
        }
        // authentication에서에서 추출된 memberId가 없다면 비회원
        if (memberId == null) {
            //비회원이면서 비밀번호를 입력하지 않았다면 예외처리, 그렇지 않다면 게시글의 비밀번호로 입력
            if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
                throw new AppException(ErrorCode.INVALID_OPERATION, "비회원은 게시글 작성 비밀번호를 입력해야 합니다.");
            }
            password = dto.getPassword();
        }else{
            author = memberLoginRepository.findActiveById(memberId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));
        }
        System.out.println("author : " + author.getMemberId());
        //입력된 게시글 저장
        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .memberId(author) // 회원인 경우 author에 MemberLogin 객체, 비회원인 경우 null
                .password(password) // 비회원인 경우 password 설정, 회원인 경우 null
                .regDate(LocalDateTime.now().withNano(0))
                .updateDate(LocalDateTime.now().withNano(0))
                .imagePaths(dto.getImagePaths())
                .build();
        System.out.println("board : " + board);
        Board savedBoard = boardRepository.save(board);
        System.out.println("savedBoard : " + savedBoard);
        //작성한 게시글을 반환
        return new ResBoardDetail(
                savedBoard.getBoardId(),
                savedBoard.getTitle(),
                savedBoard.getContent(),
                savedBoard.getMemberId() != null ? savedBoard.getMemberId().getMemberInfo().getName() : "비회원",
                savedBoard.getRegDate(),
                savedBoard.getUpdateDate(),
                savedBoard.getImagePaths()
        );
    }

    //상세 *인증 필요없음
    public ResBoardDetail getActiveBoardById(Long boardId) {
        //boardId를 확인하여 게시글을 가져오거나 없다면 예외처리
        Board board = boardRepository.findActiveById(boardId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."));

        //조회수 증가메서드
        Board updatedBoard = board.increaseViewCount();
        boardRepository.save(updatedBoard);

        //요청한 게시글 반환
        return new ResBoardDetail(
                updatedBoard.getBoardId(),
                updatedBoard.getTitle(),
                updatedBoard.getContent(),
                updatedBoard.getMemberId().getMemberInfo().getName(),
                updatedBoard.getRegDate(),
                updatedBoard.getUpdateDate(),
                updatedBoard.getImagePaths()
        );
    }

    //수정1(작성자만 수정가능) *인증 필요없음
    public ResBoardDetail updateBoard(Long boardId, ReqBoardUpdate dto) {

        //boardId를 확인하여 게시글을 가져오거나 없다면 예외처리
        Board board = boardRepository.findActiveById(boardId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."));

        //authentication객체에 SecurityContextHolder를 담아서 인증정보를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //authentication에서 memberId 추출
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            memberId = ((MemberDetails) authentication.getPrincipal()).getMemberId();
        }

        //JWT에서 추출된 memberId를 확인하고 없다면 비회원임을 확인하고 있다면 작성자인지 확인
        boolean isOwner = false;
        if (memberId != null) {
            isOwner = board.getMemberId() != null && board.getMemberId().getMemberId().equals(memberId);
        }

        //isOwner가 false이면서 입력한 비밀번호가 공란 혹은 틀렸을 경우
        if (!isOwner) {
            if (dto.getPassword() == null || !dto.getPassword().equals(board.getPassword())) {
                throw new AppException(ErrorCode.UNAUTHORIZED, "수정 권한이 없습니다.");
            }
        }

        //게시글 수정
        board.update(dto.getTitle(), dto.getContent());
        Board updatedBoard = boardRepository.save(board);

        return new ResBoardDetail(
                updatedBoard.getBoardId(),
                updatedBoard.getTitle(),
                updatedBoard.getContent(),
                updatedBoard.getMemberId() != null ? updatedBoard.getMemberId().getMemberInfo().getName() : "비회원",
                updatedBoard.getRegDate(),
                updatedBoard.getUpdateDate(),
                updatedBoard.getImagePaths()
        );
    }

    //게시글 비활성화 *인증 필요없음
    public void deactivateBoard(Long boardId, ReqBoardDeactivate dto) {
        //boardId를 확인하여 게시글을 가져오거나 없다면 예외처리
        Board board = boardRepository.findActiveById(boardId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."));

        //authentication객체에 SecurityContextHolder를 담아서 인증정보를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //authentication에서 memberId 추출
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            memberId = ((MemberDetails) authentication.getPrincipal()).getMemberId();
        }
        // authentication에서 추출된 memberId를 확인하고 없다면 비회원임을 확인, memberId가 있으나 매칭되지 않을 경우(위변조, 에러 등) 예외처리
        if (memberId != null) {
            MemberLogin member = memberLoginRepository.findActiveById(memberId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "글을 삭제할 수 있는 권한이 없습니다."));
            if(board.getMemberId() != null && board.getMemberId().getMemberId().equals(memberId)){
                auth = "author";
            }else if(Boolean.TRUE.equals(member.getIsAdmin())){
                auth = "admin";
            }
        }

        //삭제분기
        if (Objects.equals(auth, "author") || Objects.equals(auth, "admin")) {
            //파라미터 isByAdmin이 true일경우 관리자의 게시글 간접 비활성화 그렇지 않다면 게시글 작성자의 직접 비활성화
            board.deactivate(LocalDateTime.now().withNano(0), auth.equals("admin"));
        } else {
            //비회원이면서 게시글의 비밀번호가 공란 혹은 틀렸을 경우
            if (dto.getPassword() == null || !dto.getPassword().equals(board.getPassword())) {
                throw new AppException(ErrorCode.UNAUTHORIZED, "비밀번호 입력 오류입니다.");
            }
            //비회원인 게시글 작성자의 직접 비활성화
            board.deactivate(LocalDateTime.now().withNano(0), false);
        }

        boardRepository.save(board);
    }

    /*삭제*/
    public void deleteBoard(Long boardId) {
        boardRepository.deleteById(boardId);
    }

    //비활성화된 게시글 전체 List조회 (검색 및 페이징처리 포함) *인증 필요
    public Page<ResBoardList> getDeactiveBoardList(Pageable pageable, String searchType, String searchText) {
        //활성화된 게시글만 저장하는 spec 객체 생성
        Specification<Board> spec = BoardSpecifications.isDeactive();
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

    //비활성화된 상세 *인증 필요
    public ResBoardDetail getDeactiveBoardById(Long boardId) {
        //boardId를 확인하여 게시글을 가져오거나 없다면 예외처리
        Board board = boardRepository.findDeactiveById(boardId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."));

        //요청한 게시글 반환
        return new ResBoardDetail(
                board.getBoardId(),
                board.getTitle(),
                board.getContent(),
                board.getMemberId().getMemberInfo().getName(),
                board.getRegDate(),
                board.getUpdateDate(),
                board.getImagePaths()
        );
    }

    //비활성화된 게시글 복구 *인증필요
    public void activateBoard(Long boardId) {
        //boardId를 확인하여 게시글을 가져오거나 없다면 예외처리
        Board board = boardRepository.findDeactiveById(boardId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."));

        //게시글 활성화
        board.active();

        boardRepository.save(board);
    }
}
