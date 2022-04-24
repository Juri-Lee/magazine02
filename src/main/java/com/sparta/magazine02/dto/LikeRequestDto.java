package com.sparta.magazine02.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequestDto {
    @NotBlank(message = "값이 존재하지 않습니다:postId")
    private Long postId;

}

