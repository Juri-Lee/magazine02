package com.sparta.magazine02.ServiceUnitTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.magazine02.advice.RestException;
import com.sparta.magazine02.dto.PostRequestDto;
import com.sparta.magazine02.dto.PostResponseDto;
import com.sparta.magazine02.model.Posts;
import com.sparta.magazine02.service.PostService;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


// 테스트 코드 시나리오 문서
//https://docs.google.com/document/d/1FBwbhEDGFa8gvkV-qzTwLHuBy4DE81oc1dAvcJ_6rIs/edit?usp=sharing


@Nested
@DisplayName("포스트 서비스 유닛 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@Slf4j
public class PostServiceUnitTest {
    @Autowired
    private PostService postService;

    @Autowired
    ObjectMapper objectMapper;

    private Long postId;
    private String imagePath;
    private String contents;
    private String layout;
    private String username;
    private Posts createdPost;

    @BeforeEach
            void setup(){

        imagePath = "imagePath.jpg";
        contents = "성공적으로 등록되는 포스트";
        layout = "위";
        username ="testUser";
    }


    //postservice
    //  public Posts save(PostRequestDto requestDto, String username)
    @Test
    @Order(1)
    @DisplayName("포스트 작성-성공")
    void test1(){
        //given
        PostRequestDto requestdTo = new PostRequestDto(imagePath, contents, layout);

        //when
        createdPost = postService.save(requestdTo, username);
        this.postId = createdPost.getPostId();

        //then
        assertEquals(createdPost.getContents(),contents);
        assertEquals(createdPost.getImagePath(),imagePath);
        assertEquals(createdPost.getLayout(),layout);
    }

    @Test
    @Order(2)
    @DisplayName("포스트 작성시 컨텐츠 빼먹음-실패")
    void test2(){
        //given
        String missingContents = null;
        PostRequestDto requestdTo = new PostRequestDto(imagePath, missingContents, layout);
        //when
        Exception exception = Assertions.assertThrows(RuntimeException.class,
                () -> {
                    postService.save(requestdTo, username);
                });
        //then
        assertNotNull(exception);
    }
    @Test
    @Order(3)
    @DisplayName("포스트 작성시 이미지 빼먹음-실패")
    void test3(){
        //given
        String missingImagePath = null;
        PostRequestDto requestdTo = new PostRequestDto(missingImagePath, contents, layout);
        //when
        Exception exception = Assertions.assertThrows(RuntimeException.class,
                () -> {
                    postService.save(requestdTo, username);
                });
        //then
        assertNotNull(exception);
    }

    //public List<PostResponseDto> findAll()
    @Test
    @Order(4)
    @DisplayName("모든 포스트 보기-성공")
    void test4(){
        //given
        //when
        List<PostResponseDto> getallPost = postService.findAll(username);
        //then
        assertNotNull(getallPost);
    }
 //   public PostResponseDto findOne(Long postId)
    @Test
    @Order(5)
    @DisplayName("포스트 하나 열람 가능-성공")
    void test5(){
        //given
        System.out.println(this.postId);
        //when
        PostResponseDto getone = postService.findOne(postId);
        //then
        assertEquals(getone.getContents(),contents);
        assertEquals(getone.getLayout(),layout);
        assertEquals(getone.getPostId(),postId);
    }
    @Test
    @Order(6)
    @DisplayName("사용자가 자신이 작성한 포스트 수정-성공")
    //public Posts update(Long postId, PostRequestDto requestDto, String username)
    void testModifiedMine(){
        //given
        String updatedContents = " 수정된 컨텐츠 입니다. ";
        PostRequestDto requestdTo = new PostRequestDto(imagePath, updatedContents, layout);
        //when
        Posts updatepost = postService.update(postId,requestdTo,username);
        //then
        assertEquals(updatepost.getLayout(),layout);
        assertEquals(updatepost.getContents(),updatedContents);
        assertEquals(updatepost.getImagePath(),imagePath);
        assertEquals(updatepost.getPostId(),postId);
    }

    @Test
    @Order(7)
    @DisplayName("사용자가 자신이 작성하지 않은 포스트 수정-실패")
    void testModifiedYours(){
        //given
        username = "notYours";
        String updatedContents = " 수정된 컨텐츠 입니다. ";
        PostRequestDto requestdTo = new PostRequestDto(imagePath, updatedContents, layout);
        //when
        Exception exception = Assertions.assertThrows(RuntimeException.class,
                ()->{
                    postService.update(postId,requestdTo,username);
                });
        //then
        assertNotNull(exception);
    }

    //controller에서 현재 사용자의 아이디 정보를  username으로 보내준다.
    //public void delete(Long postId, String username)
    @Test
    @Order(8)
    @DisplayName("사용자가 다른유저가 작성한 포스트 삭제 -실패")
    void test7(){
        //given
        username="notYours";
        //when
        Exception exception = Assertions.assertThrows(RestException.class,
                ()->{
                    postService.delete(postId, username);
                });
        //then
        assertEquals(exception.getMessage(),"다른 사용자의 게시글을 삭제 할 수 없습니다.");
    }
    @Test
    @Order(9)
    @DisplayName("사용자가 자신이 작성한 포스트 삭제 -성공")
    void test6(){
        //given
        //when
        postService.delete(postId, username);
        Exception exception = Assertions.assertThrows(RestException.class,
                ()->{
            postService.findOne(postId);
                }
        );
        //then
        assertEquals(exception.getMessage(), "해당 postId가 존재하지 않습니다.");
    }




}
