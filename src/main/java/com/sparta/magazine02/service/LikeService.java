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
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LikeService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;


    //like 객체 삭제
    @Transactional
    public void deleteLike(LikeRequestDto requestDto, String username) {

        Posts findPost = postRepository.findByPostId(requestDto.getPostId()).orElseThrow(
                () -> new RestException(HttpStatus.NOT_FOUND, "해당 postId가 존재하지 않습니다.")
        );
        Users findUser = userRepository.findByUsername(username).orElseThrow(
                () -> new RestException(HttpStatus.NOT_FOUND, "해당 username이 존재하지 않습니다.")
        );
        Optional<Likes> findLike = likeRepository.findLikeByPost_PostIdAndUser_Username(requestDto.getPostId(), username);
        if (findLike.isPresent()) {
            likeRepository.deleteById(findLike.get().getLikeId());
        }
    }

    //포스트에 저장된 좋아요를 포스트 삭제 시에 지워주기 위해서 사용
    //postid에 해당하는 좋아요를 가져와서 전부 다 삭제 해준다 .
    @Transactional
    public void deleteLikeByPostID(Long postId){
        List<Likes> findLike = likeRepository.findAllLikeByPost_PostId(postId);
        for (Likes likes : findLike) {
            likeRepository.deleteById(likes.getLikeId());
        }
    }

    //like 객체 생성
    @Transactional
    public void saveLike(LikeRequestDto requestDto, String username ){
        Posts findPost = postRepository.findByPostId(requestDto.getPostId()).orElseThrow(
                () -> new RestException(HttpStatus.NOT_FOUND, "해당 postId가 존재하지 않습니다.")
        );
        Users findUser = userRepository.findByUsername(username).orElseThrow(
                () -> new RestException(HttpStatus.NOT_FOUND, "해당 username이 존재하지 않습니다.")
        );
        //postID 와 username 이 존재 하면 like 객체를 만들어준다음에 post 에도 좋아요를 추가하고, 해당유저에도 좋아요를 추가한다.
        Optional<Likes> findLike = likeRepository.findLikeByPost_PostIdAndUser_Username(requestDto.getPostId(), username);
        if (!findLike.isPresent()) {
            Likes like = Likes.builder()
                    .user(findUser)
                    .post(findPost)
                    .build();
            likeRepository.save(like);
            findPost.addLike(like);
            findUser.addLike(like);
        }

    }


}
