package com.exam.examBbs.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberLoginRequest {
    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 입력 형식이 바르지 않습니다.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 4, message = "비밀번호는 최소 4자이상 이어야합니다.")
    private String password;
}
