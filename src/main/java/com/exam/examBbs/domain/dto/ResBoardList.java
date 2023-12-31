package com.exam.examBbs.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResBoardList {
    private Long boardId;
    private String title;
    private String authorName;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
}
