package com.exam.examBbs.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BoardInsertRequest {
    private String title;
    private String content;
    private Long authorId;
}
