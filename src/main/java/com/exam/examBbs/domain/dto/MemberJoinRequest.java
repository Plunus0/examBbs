package com.exam.examBbs.domain.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
public class MemberJoinRequest {
    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 입력 형식이 바르지 않습니다.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 4, message = "비밀번호는 최소 4자이상 이어야합니다.")
    //regxp의 문법 확인
    @Pattern(regexp = ".*[!@#$%^&*()_+].*", message = "비밀번호에는 최소 하나의 특수문자가 포함되어야 합니다.")
    private String password;

    @NotEmpty(message = "이름은 필수 입력 값입니다.")
    @NotBlank
    private String name;

    private String contact;
    private LocalDate birthdate;
    private String gender;
    private String address;
    private boolean isAdmin;


}
