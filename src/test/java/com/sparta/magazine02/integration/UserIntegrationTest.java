package com.sparta.magazine02.integration;

import com.sparta.magazine02.dto.RegisterRequestDto;
import com.sparta.magazine02.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class UserIntegrationTest {
    @Autowired
    UserService userService;

    @Test
    @Order(1)
    @DisplayName("NL-회원가입")
    void test1(){
        //givin
        String username = "Bongnamsik";
        String password = "Bongsikpw";
        String nickname = "namsik";

        RegisterRequestDto requestDto = new RegisterRequestDto(username,password, nickname);

        //when



    }
    @Test
    @Order(2)
    @DisplayName("NL-로그인")
    void test2(){

    }
    @Test
    @Order(3)
    @DisplayName("NL-로그인")
    void test3(){

    }
    @Test
    @Order(4)
    @DisplayName("NL-로그인")
    void test4(){

    }
}
