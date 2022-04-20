package com.sparta.magazine02.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class LikeRequestDto {
    @NotBlank(message = "값이 존재하지 않습니다:postId")
    private Long postId;
}

