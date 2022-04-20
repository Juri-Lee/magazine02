package com.sparta.magazine02.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoResponseDto {
    private String username;
    private String nickname;

    public UserInfoResponseDto(String username, String nickname){
        this.username = username;
        this.nickname = nickname;
    }
}
