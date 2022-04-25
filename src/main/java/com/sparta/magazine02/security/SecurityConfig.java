package com.sparta.magazine02.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@RequiredArgsConstructor
@Configuration
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    // authenticationManager를 Bean 등록합니다.
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    @Override
//    public void configure(WebSecurity web) {
//        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
//        web
//                .ignoring()
//                .antMatchers("/h2-console/**");
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //CorsConfigurationSours 를 cors정책의 설정파일 등록하는 부분
        http
                .httpBasic().disable()
                //CORS---------------------------------------------------------------------------
                .cors().configurationSource(corsConfigurationSource())
//                .headers().frameOptions().disable()
                .and()
                .csrf().disable()
                //JWT-----------------------------------------------------------------------------
                // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeRequests()
                // home 페이지 login 없이 허용
                .antMatchers("/").permitAll()
                // 회원 관리 처리 API 전부를 login 없이 허용
                .antMatchers("/api/user/**").permitAll()
                // Get 요청 login 없이 허용
                .antMatchers(HttpMethod.GET, "/api/post").permitAll()
                .antMatchers(HttpMethod.GET, "/api/post/*").permitAll()
                //antMatchers로 설정한 조건 외의 어떤 요청이든 '인증'해야 한다
                .anyRequest().authenticated();
    }

    //---------------Cors 허용 적용----------------
    //Cors 허용 정책 설정하는 Bean
    //addAllowedOrigin() : 허용할URL
    //addAllowedHeader() : 허용할 Header
    //addAllowedMethod(): 허용할  Http Method
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;

    }
}
