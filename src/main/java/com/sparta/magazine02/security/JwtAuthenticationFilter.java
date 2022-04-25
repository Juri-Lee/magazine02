package com.sparta.magazine02.security;

import com.sparta.magazine02.advice.RestException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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


        // 토큰이 발급되어 있는 경우 유효한 토큰인지 확인합니다.
        if (accessToken != null) {
            if (jwtTokenProvider.validateToken(accessToken)) {
                //토큰을 SecurityContextHoder 의 Authentication에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else{
                //토큰 유효기간 만료
                //필터 거치면서 로그인정보를 null로 만들어 로그인 안한상태로 만들기
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }
        //전처리
        chain.doFilter(request, response);
        //후처리
    }

}
