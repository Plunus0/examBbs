package com.exam.examBbs.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReqBoardSave {
    private String title;
    private String content;
    private String password;
}
