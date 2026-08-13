package com.gukbabfc.freeboard.dto;

import com.gukbabfc.freeboard.entity.FreeBoardComment;

import java.time.LocalDateTime;

/**
 * 게시글 상세 화면에 표시할 댓글과 관리 권한 정보를 전달합니다.
 */
public record FreeBoardCommentView(
        Long id,
        String content,
        String authorName,
        String authorUsername,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean canManage
) {
    public static FreeBoardCommentView from(FreeBoardComment comment, boolean canManage) {
        return new FreeBoardCommentView(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getName(),
                comment.getAuthor().getUsername(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                canManage
        );
    }
}
