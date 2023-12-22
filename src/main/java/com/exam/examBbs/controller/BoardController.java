package com.exam.examBbs.controller;

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
import com.exam.examBbs.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bbs")
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<Page<BoardListDTO>> getBoards(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        System.out.println("controller START");
        Pageable pageable = PageRequest.of(page, size, Sort.by("boardId").descending());
        System.out.println("controller middle");
        Page<BoardListDTO> boardList = boardService.getBoardList(pageable, searchType, searchText);
        System.out.println("controller END");
        return ResponseEntity.ok().body(boardList);
    }

    //전체 조회(반환타입 변경)
/*    @GetMapping
    public List<ResponseAllBoard> getAllBoards(*//*@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int pageSize*//*) {
//        Pageable pageable = PageRequest.of(page, pageSize);
        //페이징에 필요한 데이터만 전송
        return boardService.getPaginatedBoard(*//*pageable*//*);
    }*/

    //게시글 작성
    @PostMapping("/write")
    public ResponseEntity<BoardDetailDTO> saveBoard(@RequestBody BoardSaveRequest dto) {
        BoardDetailDTO boardDetail = boardService.saveBoard(dto);
        return new ResponseEntity<>(boardDetail, HttpStatus.CREATED);
    }

    //상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable Long id) {
        return new ResponseEntity<>(boardService.getBoardById(id), HttpStatus.OK);
    }

    //업데이트
    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable Long id, @RequestBody BoardUpdateRequest dto) {
        return new ResponseEntity<>(boardService.updateBoard(id, dto), HttpStatus.OK);
    }

    //삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //api문서 작성 스웨거설치
}
