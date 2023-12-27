package com.exam.examBbs.configuration;

import com.exam.examBbs.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final MemberService memberService;
    @Value("${jwt.secret}")
    private String secretKey;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .authorizeHttpRequests(requests -> {
                    requests.anyRequest().permitAll();
//                    requests.requestMatchers("/bbs", "/members", "/members/**").permitAll();
//                    requests.requestMatchers(HttpMethod.POST, "/bbs/**").authenticated();
                })
                .sessionManagement(
                        sessionManagement ->
                                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(new JwtFilter(memberService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
/*        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests(requests ->{
                    requests.requestMatchers("/api/bbs", "/api/bbs/**", "/members/**").permitAll();
                    requests.requestMatchers(HttpMethod.POST, "/api/bbs/**").authenticated();
                    requests.requestMatchers(HttpMethod.PUT, "/api/bbs/**").authenticated();
                    requests.requestMatchers(HttpMethod.DELETE, "/api/bbs/**").authenticated();
                })
                .addFilterBefore(new JwtFilter(memberService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();*/
    }
}

        /*        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .authorizeHttpRequests(requests -> {
                    requests.anyRequest().permitAll();
//                    requests.requestMatchers("/bbs", "/members", "/members/**").permitAll();
//                    requests.requestMatchers(HttpMethod.POST, "/bbs/**").authenticated();
                })
                .sessionManagement(
                        sessionManagement ->
                                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
//                .addFilterBefore(new JwtFilter(memberService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }*/


