package com.gukbabfc.freeboard.dto;

import com.gukbabfc.freeboard.entity.FreeBoardPost;

import java.time.LocalDateTime;

public record FreeBoardListItem(
        Long id,
        String title,
        String authorName,
        LocalDateTime createdAt
) {
    public static FreeBoardListItem from(FreeBoardPost post) {
        return new FreeBoardListItem(
                post.getId(),
                post.getTitle(),
                post.getAuthor().getName(),
                post.getCreatedAt()
        );
    }
}
