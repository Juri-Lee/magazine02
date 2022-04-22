package com.sparta.magazine02.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//요청이 들어올대마다 인증을 하기 위해서 OncePerRequestFilter를 상속 받아서 구현한다
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 헤더에서 JWT 를 받아옵니다.
        String accessToken = jwtTokenProvider.resolveAccessToken(request);

        // 유효한 토큰인지 확인합니다.
        if (accessToken != null) {
            if (jwtTokenProvider.validateToken(accessToken)) {
                //토큰을 SecurityContextHoder 의 Authentication에 저장
                this.setAuthentication(accessToken);

            }
        }
        chain.doFilter(request, response);
    }

    // SecurityContext 에 Authentication 객체를 저장합니다.
    public void setAuthentication(String token) {
        // 토큰으로부터 유저 정보를 받아옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        // SecurityContext 에 Authentication 객체를 저장합니다.
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
