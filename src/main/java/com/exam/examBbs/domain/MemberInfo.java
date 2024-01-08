package com.exam.examBbs.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "member_info")
public class MemberInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_info_seq_gen")
    @SequenceGenerator(name = "member_info_seq_gen", sequenceName = "member_info_seq", initialValue = 1000001, allocationSize = 1)
    private Long memberInfoId;

    @Column(nullable = false)
    private String name;

    private String contact;
    private LocalDate birthdate;
    private String gender;
    private String address;

    @OneToOne(mappedBy = "memberInfo", fetch = FetchType.LAZY)
    private MemberLogin memberLogin;

    // MemberLogin과 연결하는 편의 메서드
    public void setMemberLogin(MemberLogin memberLogin) {
        this.memberLogin = memberLogin;
        if (memberLogin.getMemberInfo() != this) {
            memberLogin.setMemberInfo(this);
        }
    }

}
