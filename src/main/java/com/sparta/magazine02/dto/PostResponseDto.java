package com.sparta.magazine02.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.magazine02.model.Posts;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostResponseDto {
    private Long postId;
    private String imagePath;
    private String contents;
    private String nickname;
    private String layout;
    private Long likeCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;
    private boolean liked;

    public PostResponseDto(Posts posts) {
        this.postId = posts.getPostId();
        this.imagePath = posts.getImagePath();
        this.contents = posts.getContents();
        this.nickname = posts.getUser().getNickname();
        this.likeCount = (long) posts.getLikeList().size();
        this.createdAt = posts.getCreatedAt();
        this.modifiedAt = posts.getModifiedAt();
        this.layout = posts.getLayout();
        this.liked = false;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
