package com.exam.examBbs.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
    //토큰 발급
    public static String createJwt(String email, Long memberId, String secretKey, Long expiredMs){
        return Jwts.builder()
                .claim("email", email)
                .claim("memberId", memberId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    //발급된 토큰 유효시간 검증
    public static boolean isExpired(String token, String secretKey) {
        System.out.println("isExpired token = " + token);
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    //토큰에서 memberId 추출
    public static Long getUserIdFromToken(String token, String secretKey) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        System.out.println("getUserIdFromToken token = " + token);
        return Long.parseLong(claims.get("memberId").toString());
    }

    //토큰에서 email 추출
    public static String getEmailFromToken(String token, String secretKey) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        System.out.println("getEmailFromToken token = " + token + "\nmemberId = "+claims.get("memberId").toString()+"\nemail1 = "+claims.get("email").toString()+"\nemail2 = "+claims.get("email", String.class));
        return claims.get("email", String.class);
    }
}
