package com.exam.examBbs.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "member_login")
public class MemberLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_gen")
    @SequenceGenerator(name = "member_seq_gen", sequenceName = "member_seq", allocationSize = 1)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Builder.Default
    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;

    @Column(name = "deactivated_date")
    private LocalDateTime deactivatedDate;

    @Column(name = "deprecated_date")
    private LocalDateTime deprecatedDate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_info_id", referencedColumnName = "memberInfoId")
    private MemberInfo memberInfo;

    public void setMemberInfo(MemberInfo memberInfo) {
        this.memberInfo = memberInfo;
        if (memberInfo.getMemberLogin() != this) {
            memberInfo.setMemberLogin(this);
        }
    }


}