package com.sparta.magazine02.controller;

import com.sparta.magazine02.advice.RestException;
import com.sparta.magazine02.dto.LoginRequestDto;
import com.sparta.magazine02.dto.RegisterRequestDto;
import com.sparta.magazine02.dto.TokenResponseDto;
import com.sparta.magazine02.dto.UserInfoResponseDto;
import com.sparta.magazine02.model.Success;
import com.sparta.magazine02.model.Users;
import com.sparta.magazine02.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequestMapping("/api/user")
@RequiredArgsConstructor
@RestController
public class UserRestController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Success> registerUser(@AuthenticationPrincipal Users users, @Valid @RequestBody RegisterRequestDto requestDto, Errors errors) {
       //이미 사용자가 로그인 되어있을 경우 회원가입을 할 수 없다.
        if(users !=null){
            throw new RestException(HttpStatus.BAD_REQUEST, "로그아웃 후에 회원가입을 진행 해 주세요.");
        }
        //Request body에서 에러가 나면 에러를 보내준다
        if (errors.hasErrors()) {
            for (FieldError error : errors.getFieldErrors()) {
                throw new RestException(HttpStatus.BAD_REQUEST, error.getDefaultMessage());
            }
        }
        //사용자가 입력한 입력값의 유저네임이 비밀번호에 포함된다면 비밀번호를 사용할 수 없다.
        if (requestDto.getPassword().contains(requestDto.getUsername())) {
            throw new RestException(HttpStatus.BAD_REQUEST, "비밀번호 내에 아이디를 포함할 수 없습니다.");
        } else if (!requestDto.getPassword().equals( requestDto.getPasswordCheck())) {
            //사용자가 입력한 입력값의 비밀번호와 비밀번호확인은 같아야 한다.
            throw new RestException(HttpStatus.BAD_REQUEST, "비밀번호와 비밀번호확인이 일치하지 않습니다.");
        } else {
            //모든 조건이 충족될경우에 회원가입을 진행한다.
            userService.registerUser(requestDto);
            return new ResponseEntity<Success>(new Success(true, "회원가입 성공"), HttpStatus.OK);
        }
    }
    @GetMapping("/register/{username}")
    public ResponseEntity<Success> idcheck(@PathVariable String username){
            //유저네임이 등록되어있지 않은경우 사용가능 하다
            if(!userService.hasUsername(username)){
                return new ResponseEntity<Success>(new Success(true, "사용 가능한 유저네임입니다."), HttpStatus.OK);
            }
            //유저네임이 등록되어있는경우 사용불가능 하다.
        return new ResponseEntity<Success>(new Success(false, "사용불가능한 유저네임입니다."), HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<Success> login(@RequestBody LoginRequestDto requestDto, @AuthenticationPrincipal Users users,HttpServletResponse response, Errors errors) {
        //이미 로그인되어 있다면 사용자는 로그인 할 수 없다.
        if(users !=null){
            throw new RestException(HttpStatus.BAD_REQUEST, "이미 로그인된 사용자 입니다.");
        }
        //requestbody 에서 들어온 에러를 처리한다
        if (errors.hasErrors()) {
            for (FieldError error : errors.getFieldErrors()) {
                throw new RestException(HttpStatus.BAD_REQUEST, error.getDefaultMessage());
            }
        }
        //이미 로그인 되어있지 않는다면 로그인을 시행한다.
        TokenResponseDto token = userService.login(requestDto);
        //로그인이 오류없이 처리 되었다면 Autorization 토큰을 헤더에 실어 보내준다.
        response.setHeader("Authorization", token.getAuthorization());
//        System.out.println(token.getAuthorization());
        return new ResponseEntity<>(new Success(true, "로그인에 성공했습니다."), HttpStatus.OK);
    }


    @PostMapping("/logout")
    public ResponseEntity<Success> logout(HttpServletRequest request) {
        //로그아웃시에 Conttext holder에 있는 사용자 정보 컨텐츠 값을 지줘준다.
        userService.logout(request);
        return new ResponseEntity<>(new Success(true, "로그아웃 성공"), HttpStatus.OK);
    }


    @GetMapping("/info")
    public UserInfoResponseDto getinfo(@AuthenticationPrincipal Users users){
        //유저가 로그인 되어있지 않는 경우에는 유저정보를 반환하지 않는다
        if(users ==null){
            throw new RestException(HttpStatus.BAD_REQUEST, "유저정보가 존재하지 않습니다.");
        }
        //로그인 된 사용자의 이름과 닉네임을 반환한다.
        return new UserInfoResponseDto(users.getUsername(), users.getNickname());
    }
}

