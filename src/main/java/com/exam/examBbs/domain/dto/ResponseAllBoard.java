package com.exam.examBbs.domain.dto;

import com.exam.examBbs.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseAllBoard<T> {
    private Long boardId;
    private String title;
    private Member author;
    private LocalDateTime updateDate;
}
