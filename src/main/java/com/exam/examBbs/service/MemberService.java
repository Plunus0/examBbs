package com.exam.examBbs.service;

import com.exam.examBbs.domain.Member;
import com.exam.examBbs.exception.AppException;
import com.exam.examBbs.exception.ErrorCode;
import com.exam.examBbs.repository.MemberRepository;
import com.exam.examBbs.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService{

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    @Value("${jwt.secret}")
    private String secretKey;

    //토큰의 유효시간
    private Long expiredMs = 1000 * 60 * 60L;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findActiveByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // UserDetails 객체 반환
        return new User(member.getEmail(), member.getPassword(), getAuthorities(member));
    }

    public void join(String name, String password, String email){

        //email 중복 체크
        memberRepository.findActiveByEmail(email)
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
        Member selectedMember = memberRepository.findActiveByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, email + "이 없습니다."));
        //비밀번호 확인
        if(!encoder.matches(password, selectedMember.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, "비밀번호가 틀렸습니다.");
        }
        //인증성공시
        return JwtUtil.createJwt(email, selectedMember.getMemberId(), secretKey, expiredMs);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(Member member) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // isadmin 필드가 true인 경우 관리자 권한 부여
        if (Boolean.TRUE.equals(member.getIsAdmin())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            // 일반 사용자에 대한 권한 설정 (선택적)
            // 예: authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return authorities;
    }
}
