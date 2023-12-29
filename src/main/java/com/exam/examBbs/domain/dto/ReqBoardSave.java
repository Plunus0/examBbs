package com.exam.examBbs.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ReqBoardSave {
    @NotEmpty(message = "제목은 필수 입력 값입니다.")
    private String title;
    @NotEmpty(message = "내용은 필수 입력 값입니다.")
    private String content;
    private String password;
    private List<String> imagePaths;
}
