package com.sparta.magazine02.repository;

import com.sparta.magazine02.model.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Posts, Long> {
    List<Posts> findAll();
    void deleteByPostId(Long postId);
    Optional<Posts> findByPostId(Long postId);
}
