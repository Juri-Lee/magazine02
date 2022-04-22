package com.sparta.magazine02.repository;

import com.sparta.magazine02.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findLikeByPost_PostIdAndUser_Username(Long postId, String username);
}
