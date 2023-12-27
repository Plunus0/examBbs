package com.exam.examBbs.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_gen")
    @SequenceGenerator(name = "member_seq_gen", sequenceName = "member_seq", allocationSize = 1)
    @Column(name = "member_id")
    private Long memberId;
    private String name;
    private String password;
    private String email;
    @Builder.Default
    @Column(name = "reg_date")
    private LocalDateTime regDate = LocalDateTime.now();
    private LocalDateTime deactivatedDate; // 간접 비활성화 날짜
    private LocalDateTime deprecatedDate; // 직접 비활성화 날짜
    @Builder.Default
    private Boolean isAdmin = false; // 관리자 여부
}

