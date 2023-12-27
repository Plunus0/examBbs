package com.exam.examBbs.configuration;

import com.exam.examBbs.service.MemberService;
import com.exam.examBbs.utils.JwtUtil;
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

        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        logger.info("authorization = " + authorization);

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            logger.error("authorization nothing");
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorization.split(" ")[1];

        //token 꺼내기
//        String token = getTokenFromRequest(request);
        logger.info("token = " + token);

        //token이 없거나 만료되었는지 확인 후 권한부여 및 Detail을 추가한다.
        if (token != null && !JwtUtil.isExpired(token, secretKey)) {
            try {
                String email = JwtUtil.getEmailFromToken(token, secretKey);
                UserDetails userDetails = memberService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: JWT validation failed");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim(); // "Bearer " 이후의 문자열(토큰)을 반환
        }
        return null;
    }
}
/*        // Token Expired 되었는지 여부
        if(token != null && JwtUtil.isExpired(token, secretKey)){
            logger.error("Token Expired");
            filterChain.doFilter(request, response);
            return;
        }

        //Token에서 email && id 가져오기
        Long memberId;
        String email = "";

        //권한부여
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority("MEMBER")));

        //Detail 추가
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);*/



