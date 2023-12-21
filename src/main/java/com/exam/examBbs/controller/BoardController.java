package com.exam.examBbs.controller;

import com.exam.examBbs.domain.dto.ResponseAllBoard;
import com.exam.examBbs.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bbs")
public class BoardController {
    private final BoardService boardService;

    //전체 조회(반환타입 변경)
    @GetMapping
    public List<ResponseAllBoard> getAllBoards(/*@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int pageSize*/) {
//        Pageable pageable = PageRequest.of(page, pageSize);
        //페이징에 필요한 데이터만 전송
        return boardService.getPaginatedBoard(/*pageable*/);
    }
//
//    //게시글 작성
//    @PostMapping("/write")
//    public ResponseEntity<Board> saveBoard(@RequestBody BoardInsertRequest dto) {
//        return new ResponseEntity<>(boardService.saveBoard(dto), HttpStatus.CREATED);
//    }
//
//    //상세 조회
//    @GetMapping("/{id}")
//    public ResponseEntity<Board> getBoardById(@PathVariable Long id) {
//        return new ResponseEntity<>(boardService.getBoardById(id), HttpStatus.OK);
//    }
//
//    //업데이트
//    @PutMapping("/{id}")
//    public ResponseEntity<Board> updateBoard(@PathVariable Long id, @RequestBody BoardUpdateRequest dto) {
//        return new ResponseEntity<>(boardService.updateBoard(id, dto), HttpStatus.OK);
//    }
//
//    //삭제
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
//        boardService.deleteBoard(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

    //api문서 작성
}
