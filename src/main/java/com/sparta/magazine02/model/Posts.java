package com.sparta.magazine02.model;

import com.sparta.magazine02.dto.PostRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Posts extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private String imagePath;

    @Column(nullable = false)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    private Users user;

    //cascade가 연관된 Entity에 작업을 수행해준다.
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 5)
    private List<Likes> likeList = new ArrayList<>();

    public void addLike(Likes like) {
        this.likeList.add(like);
        like.setPost(this);
    }

    @Builder
    public Posts(String imagePath, String contents) {
        this.imagePath = imagePath;
        this.contents = contents;
    }

    public void update(PostRequestDto requestDto) {
        this.imagePath = requestDto.getImagePath();
        this.contents = requestDto.getContents();
    }
}