package com.exam.examBbs.service;

import com.exam.examBbs.domain.MemberInfo;
import com.exam.examBbs.domain.MemberLogin;
import com.exam.examBbs.domain.dto.MemberJoinRequest;
import com.exam.examBbs.exception.AppException;
import com.exam.examBbs.exception.ErrorCode;
import com.exam.examBbs.repository.MemberInfoRepository;
import com.exam.examBbs.repository.MemberLoginRepository;
import com.exam.examBbs.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService{

    private final MemberLoginRepository memberLoginRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final BCryptPasswordEncoder encoder;
    @Value("${jwt.secret}")
    private String secretKey;
    //토큰의 유효시간
    private Long expiredMs = 1000 * 60 * 60L;

    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    //회원가입 *인증 필요없음 *인증정보 필요없음
    @Transactional
    public void join(MemberJoinRequest dto){

        //email 중복 체크
        memberLoginRepository.findActiveByEmail(dto.getEmail())
                .ifPresent(memberLogin -> {
                    throw new AppException(ErrorCode.EMAIL_DUPLICATED, dto.getEmail()+"은 등록된 이메일입니다.");
                });

        //저장
        // MemberInfo 생성 및 저장
        MemberInfo memberInfo = MemberInfo.builder()
                .name(dto.getName())
                .contact(dto.getContact())
                .birthdate(dto.getBirthdate())
                .gender(dto.getGender())
                .address(dto.getAddress())
//                .memberLogin(memberLogin) // MemberLogin 객체 연결
                .build();

        // MemberLogin을 먼저 생성하지만 저장하지 않음
        MemberLogin memberLogin = MemberLogin.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .regDate(LocalDateTime.now())
//                .isAdmin(true) // isAdmin 값 설정
                .memberInfo(memberInfo)
                .build();

        // MemberInfo 저장
        memberInfoRepository.save(memberInfo);
        memberLoginRepository.save(memberLogin);
    }

    //로그인 *인증 필요없음 *인증정보 필요없음
    public String login(String email, String password){
        //인증과정
        //이메일 확인
        MemberLogin selectedMember = memberLoginRepository.findActiveByEmail(email)
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
        MemberLogin memberLogin = memberLoginRepository.findActiveByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("등록되어 있지 않은 사용자입니다."));

        // UserDetails 객체 생성 및 반환
        return new MemberDetails(memberLogin.getMemberId(), memberLogin.getEmail(), memberLogin.getPassword(), getAuthorities(memberLogin));
    }

    //차등 권한부여
    public Collection<? extends GrantedAuthority> getAuthorities(MemberLogin memberLogin) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        //추후 master컬럼을 추가하여 이를 반영하고 최고권한을 부여

        //isadmin 필드가 true인 경우 관리자 권한 부여
        if (Boolean.TRUE.equals(memberLogin.getIsAdmin())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            logger.info("is admin");
        } else {
            //일반 사용자에 대한 권한 설정 (선택적)
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            logger.info("is user");
        }

        return authorities;
    }
}