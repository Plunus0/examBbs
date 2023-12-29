package com.exam.examBbs.controller;

import com.exam.examBbs.domain.dto.*;
import com.exam.examBbs.service.BoardService;
import com.exam.examBbs.service.FileUploadService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bbs")
public class BoardController {
    private final BoardService boardService;
    private final FileUploadService fileUploadService;
    @Value("${jwt.secret}")
    private String secretKey;

    //게시글 전체 List 조회
    @GetMapping
    public ResponseEntity<Page<ResBoardList>> getActiveBoardList(
            @RequestParam(value = "page", defaultValue = "1")
            @Min(value = 1, message = "최소 페이지는 1페이지 입니다.") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        //Pageable은 시작 페이지가 0, 따라서 페이지를 넘김받을때 1이상의 수로 받기 때문에 -1처리를 해서 0페이지 시작으로 맞춤
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("boardId").descending());
        Page<ResBoardList> boardList = boardService.getActiveBoardList(pageable, searchType, searchText);
        return ResponseEntity.ok().body(boardList);
    }

    //게시글 작성
    @PostMapping("/write")
    public ResponseEntity<ResBoardDetail> saveBoard(@Valid @RequestBody ReqBoardSave dto) {
        ResBoardDetail boardDetail = boardService.saveBoard(dto);
        return new ResponseEntity<>(boardDetail, HttpStatus.CREATED);
    }

    //게시글 상세조회
    @GetMapping("/{boardId}")
    public ResponseEntity<ResBoardDetail> getActiveBoardById(@PathVariable Long boardId) {
        return new ResponseEntity<>(boardService.getActiveBoardById(boardId), HttpStatus.OK);
    }

    //게시글 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<ResBoardDetail> updateBoard(@PathVariable Long boardId,
                                                      @Valid @RequestBody ReqBoardUpdate dto) {
        ResBoardDetail updatedBoard = boardService.updateBoard(boardId, dto);
        return ResponseEntity.ok(updatedBoard);
    }

    //게시글 비활성화
    @PostMapping("/{boardId}")
    public ResponseEntity<String> deactivateBoard(@PathVariable Long boardId,
                                                  @Valid @RequestBody ReqBoardDeactivate dto) {
        boardService.deactivateBoard(boardId, dto);
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    //이미지 업로드
    @PostMapping("/{boardId}/imageUpload")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") List<MultipartFile> files) throws IOException {
        if (files.size() > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일은 최대 5개까지 업로드할 수 있습니다.");
        }
        List<String> filepaths = fileUploadService.uploadFiles(files);

        return ResponseEntity.ok(filepaths);
    }

    //게시글 삭제
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //간접 비활성화된 게시글 전체 List 조회(관리자만 접근)
    @GetMapping("/deactive")
    public ResponseEntity<Page<ResBoardList>> getDeactiveBoardList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("boardId").descending());
        Page<ResBoardList> boardList = boardService.getDeactiveBoardList(pageable, searchType, searchText);
        return ResponseEntity.ok().body(boardList);
    }
    //간접 비활성화된 게시글 상세조회(관리자만 접근)
    @GetMapping("/deactive/{boardId}")
    public ResponseEntity<ResBoardDetail> getDeactiveBoardById(@PathVariable Long boardId) {
        return new ResponseEntity<>(boardService.getDeactiveBoardById(boardId), HttpStatus.OK);
    }
    //간접 비활성화된 게시글 활성화하기(관리자만 접근)
    @PostMapping("/deactive/{boardId}")
    public ResponseEntity<String> activateBoard(@PathVariable Long boardId) {
        boardService.activateBoard(boardId);
        return ResponseEntity.ok("게시글이 복구되었습니다.");
    }
}
