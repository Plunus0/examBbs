package com.exam.examBbs.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BoardSaveRequest {
    private String title;
    private String content;
}
