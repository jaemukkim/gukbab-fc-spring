package com.gukbabfc.freeboard.dto;

import com.gukbabfc.freeboard.entity.FreeBoardPost;

import java.time.LocalDateTime;

/**
 * 자유게시판 목록의 게시글 한 건을 표현합니다.
 */
public record FreeBoardListItem(
        Long id,
        String title,
        String authorName,
        LocalDateTime createdAt,
        long commentCount
) {
    public static FreeBoardListItem from(FreeBoardPost post, long commentCount) {
        return new FreeBoardListItem(
                post.getId(),
                post.getTitle(),
                post.getAuthor().getName(),
                post.getCreatedAt(),
                commentCount
        );
    }
}
