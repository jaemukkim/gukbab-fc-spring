package com.gukbabfc.freeboard.dto;

import com.gukbabfc.freeboard.entity.FreeBoardPost;

import java.time.LocalDateTime;

/**
 * 자유게시글 상세 화면에 전달할 정보를 담습니다.
 */
public record FreeBoardDetail(
        Long id,
        String title,
        String content,
        String authorUsername,
        String authorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FreeBoardDetail from(FreeBoardPost post) {
        return new FreeBoardDetail(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getUsername(),
                post.getAuthor().getName(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
