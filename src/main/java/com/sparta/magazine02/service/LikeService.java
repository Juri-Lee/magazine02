package com.sparta.magazine02.service;


import com.sparta.magazine02.advice.RestException;
import com.sparta.magazine02.dto.LikeRequestDto;
import com.sparta.magazine02.model.Likes;
import com.sparta.magazine02.model.Posts;
import com.sparta.magazine02.model.Users;
import com.sparta.magazine02.repository.LikeRepository;
import com.sparta.magazine02.repository.PostRepository;
import com.sparta.magazine02.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LikeService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;


    //like 객체 삭제
    @Transactional
    public Optional<Likes> deleteLike(LikeRequestDto requestDto, String username) {

        //존재하지 않는 포스트일때
        Posts findPost = postRepository.findByPostId(requestDto.getPostId()).orElseThrow(
                () -> new RestException(HttpStatus.NOT_FOUND, "해당 postId가 존재하지 않습니다.")
        );
        //로그인이 안되어있을때
        Users findUser = userRepository.findByUsername(username).orElseThrow(
                () -> new RestException(HttpStatus.NOT_FOUND, "해당 username이 존재하지 않습니다.")
        );
        Optional<Likes> findLike = likeRepository.findLikeByPost_PostIdAndUser_Username(requestDto.getPostId(), username);
        //Like객체가 존재할때
        if (findLike.isPresent()) {
            likeRepository.deleteById(findLike.get().getLikeId());
        }else{
            throw new RestException(HttpStatus.NOT_FOUND,"존재하지 않는 좋아요 입니다");
        }
        return  findLike;
    }


    //like 객체 생성
    @Transactional
    public Likes saveLike(LikeRequestDto requestDto, String username ){
        Likes like = null;

        //post가 없을때
        Posts findPost = postRepository.findByPostId(requestDto.getPostId()).orElseThrow(
                () -> new RestException(HttpStatus.NOT_FOUND, "해당 postId가 존재하지 않습니다.")
        );
        //회원이 아닐때
        Users findUser = userRepository.findByUsername(username).orElseThrow(
                () -> new RestException(HttpStatus.NOT_FOUND, "해당 username이 존재하지 않습니다.")
        );
        //postID 와 username 이 존재 하면 like 객체를 만들어준다음에 post 에도 좋아요를 추가하고, 해당유저에도 좋아요를 추가한다.
        Optional<Likes> findLike = likeRepository.findLikeByPost_PostIdAndUser_Username(requestDto.getPostId(), username);
        if (!findLike.isPresent()) {
            like = Likes.builder()
                    .user(findUser)
                    .post(findPost)
                    .build();
            likeRepository.save(like);
            findPost.addLike(like);
            findUser.addLike(like);

        }else{
            throw new RestException(HttpStatus.BAD_REQUEST,"이미 좋아요 되어있습니다");
        }
        return like;
    }


}
