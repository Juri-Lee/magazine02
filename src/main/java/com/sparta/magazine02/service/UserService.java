package com.sparta.magazine02.service;

import com.sparta.magazine02.advice.RestException;
import com.sparta.magazine02.dto.LoginRequestDto;
import com.sparta.magazine02.dto.RegisterRequestDto;
import com.sparta.magazine02.dto.TokenResponseDto;
import com.sparta.magazine02.dto.UserInfoResponseDto;
import com.sparta.magazine02.model.Users;
import com.sparta.magazine02.repository.UserRepository;
import com.sparta.magazine02.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void registerUser(RegisterRequestDto requestDto) {

        userRepository.save(Users.builder()
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .build());
    }

    //이미존재하는 유저네임이라면 true, 존재하지 않는 유저네임이라면 false를 반환한다.
    @Transactional
    public boolean hasUsername(String username){
       if( !userRepository.findByUsername(username).isPresent()){
           return false;
        }
       return true;
    }

    @Transactional
    //login 성공시에 TokenResponseDto를 반환하면 controller 에서 헤더에 실어 보내준다.
    public TokenResponseDto login(LoginRequestDto requestDto) {
    //가입되지 않은 회원정보로 로그인 시도 하면 안됨
    Users user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new RestException(HttpStatus.BAD_REQUEST, "가입되지 않은 username 입니다."));
     //회원정보와 작성한 비밀번호가 일치하지 않으면 안됨!
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new RestException(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다.");
        }
        //회원가입 되있고, 비밀번호를 잘 작성했다면 토큰을 만들어서 반환
        String accessToken = jwtTokenProvider.createAccessToken(user.getUsername());


        return TokenResponseDto.builder()
                .Authorization(accessToken)
                .build();
    }

    @Transactional
    public void logout(HttpServletRequest request){
        SecurityContextHolder.clearContext();
    }


}


