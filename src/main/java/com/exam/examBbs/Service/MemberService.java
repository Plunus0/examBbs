package com.exam.examBbs.Service;

import com.exam.examBbs.Repository.MemberRepository;
import com.exam.examBbs.domain.Member;
import com.exam.examBbs.exception.AppException;
import com.exam.examBbs.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    public final MemberRepository memberRepository;
    public void join(String name, String password, String email){

        //email 중복 체크
        memberRepository.findByEmail(email)
                .ifPresent(member -> {
                    throw new AppException(ErrorCode.EMAIL_DUPLICATED, email+"은 등록된 이메일입니다.");
                });
        //저장
        Member member = Member.builder()
                        .name(name)
                        .password(password)
                        .email(email)
                        .build();
        memberRepository.save(member);
    }
}
