package com.exam.examBbs.configuration;

import com.exam.examBbs.service.MemberService;
import com.exam.examBbs.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final MemberService memberService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //헤더에 AUTHORIZATION로 전송받은 데이터를 가져온다.
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        logger.info("authorization = " + authorization);

        //AUTHORIZATION로 전송받은 데이터가 없거나 접두사가 "Bearer "가 아니라면 return
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            logger.error("authorization nothing");
            filterChain.doFilter(request, response);
            return;
        }

        //token 꺼내기("Bearer " 접두사 없애기)
        String token = authorization.split(" ")[1];

        try {
            if (token != null && !JwtUtil.isExpired(token, secretKey)) {
                String email = JwtUtil.getEmailFromToken(token, secretKey);
                UserDetails userDetails = memberService.loadUserByUsername(email);

                //가져온 정보들을 authentication에 담아서 SecurityContextHolder에 저장
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // 토큰이 만료된 경우(401 Unauthorized)
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증만료: JWT 인증에 실패했습니다.");
            }
        } catch (ExpiredJwtException e) {
            // JWT 토큰 만료 예외 처리(401 Unauthorized)
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증만료: JWT 인증에 실패했습니다.");
        } catch (JwtException e) {
            // 기타 JWT 관련 예외 처리(403 Forbidden)
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "유효하지 않은 토큰: JWT 인증 실패");
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim(); // "Bearer " 이후의 문자열(토큰)을 반환
        }
        return null;
    }
}