package com.gukbabfc.notice.dto;

import com.gukbabfc.notice.entity.Notice;

import java.time.LocalDateTime;

/**
 * 공지사항 상세 화면에 전달할 정보를 담습니다.
 */
public record NoticeDetail(
        Long id,
        String title,
        String content,
        String authorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static NoticeDetail from(Notice notice) {
        return new NoticeDetail(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getAuthor().getName(),
                notice.getCreatedAt(),
                notice.getUpdatedAt()
        );
    }
}
