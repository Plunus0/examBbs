package com.exam.examBbs.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDetailDTO {
    private Long boardId;
    private String title;
    private String content;
    private String author;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
}