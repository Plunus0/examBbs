package com.exam.examBbs.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReqBoardUpdate {
    private String title;
    private String content;
    private String password;
}
