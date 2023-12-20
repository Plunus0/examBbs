package com.exam.examBbs.Domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberJoinRequest {
    private String name;
    private String password;
    private String email;
}
