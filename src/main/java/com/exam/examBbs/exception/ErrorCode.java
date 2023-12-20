package com.exam.examBbs.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    EMAIL_DUPLICATED(HttpStatus.CONFLICT,"");
    private HttpStatus httpStatus;
    private String message;
}
