package com.sparta.magazine02.repository;

import com.sparta.magazine02.model.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Posts, Long> {
    List<Posts> findAll();
    void deleteByPostId(Long postId);
    Optional<Posts> findByPostId(Long postId);

    @Query("SELECT p FROM Posts p LEFT JOIN p.likeList l WHERE p.postId = l.post.postId GROUP BY p.postId ORDER BY COUNT(l.post.postId) DESC, p.createdAt")
    List<Posts> findAllByLikeCount();
}


