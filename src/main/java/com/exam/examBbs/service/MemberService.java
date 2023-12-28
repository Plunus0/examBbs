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

    //회원가입 *인증 필요없음 *인증정보 필요없음
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

    //로그인 *인증 필요없음 *인증정보 필요없음
    public String login(String email, String password){
        //인증과정
        //이메일 확인
        Member selectedMember = memberRepository.findActiveByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, email + "이 없습니다."));
        //비밀번호 확인
        if(!encoder.matches(password, selectedMember.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, "비밀번호가 틀렸습니다.");
        }
        //인증성공시 토큰 발급
        return JwtUtil.createJwt(email, secretKey, expiredMs);
    }

    //인증정보에 담을 UserDetails 객체 생성 메서드
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findActiveByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("등록되어 있지 않은 사용자입니다."));

        // UserDetails 객체 생성 및 반환
        return new MemberDetails(member.getMemberId(), member.getEmail(), member.getPassword(), getAuthorities(member));
    }

    //차등 권한부여
    public Collection<? extends GrantedAuthority> getAuthorities(Member member) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        //추후 master컬럼을 추가하여 이를 반영하고 최고권한을 부여

        //isadmin 필드가 true인 경우 관리자 권한 부여
        if (Boolean.TRUE.equals(member.getIsAdmin())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            //일반 사용자에 대한 권한 설정 (선택적)
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return authorities;
    }
}