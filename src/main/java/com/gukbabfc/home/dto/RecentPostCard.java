package com.gukbabfc.home.dto;

import com.gukbabfc.freeboard.entity.FreeBoardPost;

import java.time.LocalDateTime;

/**
 * 대시보드의 최근 자유게시글 한 건과 댓글 수를 표현합니다.
 */
public record RecentPostCard(
        Long id,
        String title,
        String authorName,
        LocalDateTime createdAt,
        long commentCount
) {
    public static RecentPostCard from(FreeBoardPost post, long commentCount) {
        return new RecentPostCard(
                post.getId(),
                post.getTitle(),
                post.getAuthor().getName(),
                post.getCreatedAt(),
                commentCount
        );
    }
}
