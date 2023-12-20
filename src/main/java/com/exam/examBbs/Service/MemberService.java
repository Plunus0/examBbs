package com.exam.examBbs.Service;

import com.exam.examBbs.Domain.Member;
import com.exam.examBbs.Exception.AppException;
import com.exam.examBbs.Exception.ErrorCode;
import com.exam.examBbs.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    public void join(String name, String password, String email){

        //email 중복 체크
        memberRepository.findByEmail(email)
                .ifPresent(member -> {
                    throw new AppException(ErrorCode.EMAIL_DUPLICATED, email+"은 등록된 이메일입니다.");
                });
        //저장
        Member member = Member.builder()
                        .name(name)
                        .password(encoder.encode(password))
                        .email(email)
                        .build();
        memberRepository.save(member);
    }
}
