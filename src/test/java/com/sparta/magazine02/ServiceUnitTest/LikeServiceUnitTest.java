package com.sparta.magazine02.ServiceUnitTest;

// 테스트 코드 시나리오 문서
//https://docs.google.com/document/d/1FBwbhEDGFa8gvkV-qzTwLHuBy4DE81oc1dAvcJ_6rIs/edit?usp=sharing


import com.sparta.magazine02.advice.RestException;
import com.sparta.magazine02.dto.LikeRequestDto;
import com.sparta.magazine02.dto.PostRequestDto;
import com.sparta.magazine02.model.Likes;
import com.sparta.magazine02.model.Posts;
import com.sparta.magazine02.service.LikeService;
import com.sparta.magazine02.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@Nested
@DisplayName("좋아요 서비스 유닛 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@Slf4j
public class LikeServiceUnitTest {
    @Autowired
    PostService postService;

    @Autowired
    LikeService likeService;

    private Long postId;
    private String username ="testUser";
    private Posts createdPost;
    private LikeRequestDto likeRequestDto;

   @BeforeAll
    public void setup(){
        String imagePath = "LikeTest.jpg";
        String contents = "좋아요 등록 테스트 포스트";
        String layout = "top";
        PostRequestDto requestdTo = new PostRequestDto(imagePath, contents, layout);
        //post 생성
        createdPost = postService.save(requestdTo, username);
        postId = createdPost.getPostId();
        likeRequestDto = new LikeRequestDto(postId);
        System.out.println("셋팅 시헹 ");
       System.out.println(postId);
    }

    //public Likes saveLike(LikeRequestDto requestDto, String username )
    @Test
    @Order(1)
    @DisplayName("사용자가 좋아요를 누르면 좋아요 활성화")
    void saveLikeTest() {
        //given
        //when
        Likes like = likeService.saveLike(likeRequestDto,username);
        //then
        assertEquals(like.getUser().getUsername(),username);
        assertEquals(like.getPost().getPostId(),createdPost.getPostId());
    }

    @Test
    @Order(2)
    @DisplayName("좋아요 누른 포스트에 좋아요 저장시 에러 출력")
    // public Optional<Likes> deleteLike(LikeRequestDto requestDto, String username)
    void dubleSaveLike(){
       //given
        //when
        Exception exception = Assertions.assertThrows(RestException.class,
                ()->{
                    likeService.saveLike(likeRequestDto,username);
                });
        //then
        assertEquals(exception.getMessage(),"이미 좋아요 되어있습니다");
    }

    @Test
    @Order(3)
    @DisplayName("사용자가 좋아요를 두번 누르면 좋아요 삭제")
    void deleteLike() {
        //given
        //when
        Optional<Likes> deletedLike  = likeService.deleteLike(likeRequestDto,username);
        assertNotNull(deletedLike);
    }
    @Test
    @Order(4)
    @DisplayName("가입되지 않은 사용자가 좋아요를 누름")
    void unexistUserLike() {
       //given
        String newusername ="notExist";
        //when
        Exception exception = Assertions.assertThrows(RestException.class,
                ()->{
                    likeService.saveLike(likeRequestDto,newusername);
                });
        //then
        assertEquals(exception.getMessage(),"해당 username이 존재하지 않습니다.");
    }
    @Test
    @Order(5)
    @DisplayName("존재하지 않는 포스트에 좋아요를 누름")
    void UnexistPostLike() {
//given
        Long newPostId = -3L;
        likeRequestDto = new LikeRequestDto(newPostId);
        //when
        Exception exception = Assertions.assertThrows(RestException.class,
                ()->{
                    likeService.saveLike(likeRequestDto,username);
                });
        assertEquals(exception.getMessage(),"해당 postId가 존재하지 않습니다.");
    }

    @AfterAll
    public void done(){
        //테스트 종료후 생성된 포스트 삭제
        postService.delete(postId, username);
        System.out.println("셋팅 종료");

    }
}
