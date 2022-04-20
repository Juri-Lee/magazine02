package com.sparta.magazine02.dto;

import com.sparta.magazine02.model.Posts;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    @NotBlank(message = "image가 존재 하지 않습니다.")
    private String imagePath;
    @NotNull(message = "내용이 존재하지 않습니다.")
    private String contents;

    public Posts toEntity() {
        return Posts.builder()
                .imagePath(imagePath)
                .contents(contents)
                .build();
    }
}
