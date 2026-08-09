package com.gukbabfc.freeboard.entity;

import com.gukbabfc.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원이 작성한 자유게시글을 저장하는 JPA 엔티티입니다.
 */
@Getter
@Entity
@Table(name = "free_board_posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FreeBoardPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member author;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public FreeBoardPost(String title, String content, Member author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
}
