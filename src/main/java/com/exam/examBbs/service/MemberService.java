package com.exam.examBbs.service;

import com.exam.examBbs.domain.Member;
import com.exam.examBbs.exception.AppException;
import com.exam.examBbs.exception.ErrorCode;
import com.exam.examBbs.repository.MemberRepository;
import com.exam.examBbs.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    @Value("${jwt.secret}")
    private String secretKey;

    //토큰의 유효시간
    private Long expiredMs = 1000 * 60 * 60L;
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

    public String login(String email, String password){
        //인증과정
        //이메일 확인
        Member selectedMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, email + "이 없습니다."));
        //비밀번호 확인
        if(!encoder.matches(password, selectedMember.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, "비밀번호가 틀렸습니다.");
        }
        //인증성공시
        return JwtUtil.createJwt(email, secretKey, expiredMs);
    }
}
