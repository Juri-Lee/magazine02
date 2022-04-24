package com.sparta.magazine02.service;

import com.sparta.magazine02.advice.RestException;
import com.sparta.magazine02.dto.PostRequestDto;
import com.sparta.magazine02.dto.PostResponseDto;
import com.sparta.magazine02.model.Posts;
import com.sparta.magazine02.model.Users;
import com.sparta.magazine02.repository.LikeRepository;
import com.sparta.magazine02.repository.PostRepository;
import com.sparta.magazine02.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //모든 포스트를 반환한다
    //반환할때는 createdAt으로 내림차순으로 반환
    @Transactional
    public List<PostResponseDto> findAll() {

        List<Posts> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<PostResponseDto> responseDto = new ArrayList<>();
        for (Posts post : posts) {
            PostResponseDto postResponseDto = new PostResponseDto(post);
            responseDto.add(postResponseDto);
        }
        return responseDto;
    }


    //해당되는 포스트 아이디를 가진 포스트만 반환한다
    @Transactional
    public PostResponseDto findOne(Long postId) {
        Posts post = postRepository.findByPostId(postId).orElseThrow(
                () -> new RestException(HttpStatus.NOT_FOUND, "해당 postId가 존재하지 않습니다.")
        );
        return new PostResponseDto(post);
    }


    // 포스트 등록
    @Transactional
    public Posts save(PostRequestDto requestDto, String username) {
        //포스트를 등록할때는 유저가 있어야 한다.
        Users result = userRepository.findByUsername(username).orElseThrow(
                () -> new RestException(HttpStatus.NOT_FOUND, "해당 username이 존재하지 않습니다.")
        );
        //해당정보로 포스트를 생성한다
        Posts post = Posts.builder()
                .contents(requestDto.getContents())
                .imagePath(requestDto.getImagePath())
                .layout(requestDto.getLayout())
                .build();
        //포스트를 저장한다
        postRepository.save(post);
        //유저가 가지고 있는 포스트리스트에 포스트를 추가한다
        result.addPost(post);

        return post;
    }

    @Transactional
    public Posts delete(Long postId, String username) {
        //지우려는 포스트 찾기
        Posts post = postRepository.findByPostId(postId).orElseThrow(
                () -> new RestException(HttpStatus.NOT_FOUND, "해당 postId가 존재하지 않습니다.")
        );
        //지우려는 포스트의 글쓴이가 지금 username과 같은지 확인
        if(!post.getUser().getUsername().equals(username)){
            throw new RestException(HttpStatus.BAD_REQUEST, "다른 사용자의 게시글을 삭제 할 수 없습니다.");
        }
        //같다면 지움
        postRepository.deleteByPostId(postId);
        return post;
    }

    @Transactional
    public Posts update(Long postId, PostRequestDto requestDto, String username) {
        Posts post = postRepository.findByPostId(postId).orElseThrow(
                () -> new RestException(HttpStatus.NOT_FOUND, "해당 postId가 존재하지 않습니다.")
        );
        if (post.getUser().getUsername().equals(username)) {
            post.update(requestDto);
        } else {
            throw new RestException(HttpStatus.BAD_REQUEST, "username이 일치하지 않습니다.");
        }
        return post;
    }
}
