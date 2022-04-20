package com.sparta.magazine02.controller;

import com.sparta.magazine02.advice.RestException;
import com.sparta.magazine02.dto.PostRequestDto;
import com.sparta.magazine02.dto.PostResponseDto;
import com.sparta.magazine02.model.Success;
import com.sparta.magazine02.model.Users;
import com.sparta.magazine02.service.LikeService;
import com.sparta.magazine02.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/post")
@RequiredArgsConstructor
@RestController
public class PostRestController {
    private final PostService postService;
    private final LikeService likeService;

    //내림차순으로 정렬된 포스트를 반환한다.
    @GetMapping("")
    public List<PostResponseDto> findPostAll(HttpServletRequest request) {

        //모든 사용자가 열람할 수 있다.
            return postService.findAll();
    }


    //postId에 해당하는 것만 반환
    @GetMapping("/{postId}")
    public PostResponseDto findPost(@PathVariable Long postId) {
            return postService.findOne(postId);
    }


    @PostMapping("")
    public ResponseEntity<Success> savePost(@RequestBody @Valid PostRequestDto requestDto, @AuthenticationPrincipal Users users, Errors errors) {
        //brequestbody에 에러가 있으면 에러를 반환
        if (errors.hasErrors()) {
            for (FieldError error : errors.getFieldErrors()) {
                throw new RestException(HttpStatus.BAD_REQUEST, error.getDefaultMessage());
            }
        }
        //검증 완료후 포스트 등록
        postService.save(requestDto, users.getUsername());
        return new ResponseEntity<>(new Success(true, "게시글이 저장되었습니다."), HttpStatus.OK);
    }


    @DeleteMapping("/{postId}")
    public ResponseEntity<Success> deletePost(@PathVariable Long postId,@AuthenticationPrincipal Users users) {
        //현재 사용자와 게시글의 사용자가 다르면 게시글을 삭제 할 수 없다.

        //해당 포스트 아이디 삭제
        postService.delete(postId,users.getUsername());
        //해당 포스트가 삭제될 때 포스트를 누른 좋아요도 삭제 해준다.
        likeService.deleteLikeByPostID(postId);
        return new ResponseEntity<>(new Success(true, "게시글이 삭제되었습니다."), HttpStatus.OK);
    }


    @PutMapping("/{postId}")
    public ResponseEntity<Success> updatePost(@PathVariable Long postId, @RequestBody @Valid PostRequestDto requestDto, @AuthenticationPrincipal Users users, Errors errors) {
        //Request Body 에서 나온 에러가 있다면 에러를 반환.
        if (errors.hasErrors()) {
            for (FieldError error : errors.getFieldErrors()) {
                throw new RestException(HttpStatus.BAD_REQUEST, error.getDefaultMessage());
            }
        }
        // 사용자가 적은 정보로 존재하는 게시글을 바꿔치기 해준다. (수정)
        postService.update(postId, requestDto, users.getUsername());
        return new ResponseEntity<>(new Success(true, "게시글 수정 성공"), HttpStatus.OK);
    }
}

