package com.sparta.magazine02.controller;

import com.sparta.magazine02.advice.RestException;
import com.sparta.magazine02.dto.LikeRequestDto;
import com.sparta.magazine02.model.Likes;
import com.sparta.magazine02.model.Success;
import com.sparta.magazine02.model.Users;
import com.sparta.magazine02.repository.LikeRepository;
import com.sparta.magazine02.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LikeRestController {

    private final LikeService likeService;


    @PostMapping("/api/post/like")
    public ResponseEntity<Success> postLike(@RequestBody LikeRequestDto requestDto, @AuthenticationPrincipal Users users) {
        //로그인 되어있지 않다면 좋아요 할 수 없다.
        if(users == null){
            throw new RestException(HttpStatus.NOT_FOUND, "로그인 후 이용가능 합니다. ");
        }
        //유저 이름과 포스트 아이디를 담은 좋아요를 생성해준다.
        likeService.saveLike(requestDto, users.getUsername());
        return new ResponseEntity<>(new Success(true, "좋아요 성공"), HttpStatus.OK);
    }

    @DeleteMapping("api/post/like")
    public ResponseEntity<Success> deleteLike(@RequestBody LikeRequestDto requestDto, @AuthenticationPrincipal Users users){
        //로그인 되어있지 않다면 좋아요를 삭제 할 수없다
        if(users == null){
            throw new RestException(HttpStatus.NOT_FOUND, "로그인 후 이용가능 합니다. ");
        }
        //좋아요를 삭제해서 없애준다.
        likeService.deleteLike(requestDto, users.getUsername());
        return new ResponseEntity<>(new Success(true, "좋아요 삭제 성공"), HttpStatus.OK);
    }
}
