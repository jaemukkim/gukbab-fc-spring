package com.gukbabfc.notice.dto;

import com.gukbabfc.notice.entity.Notice;

import java.time.LocalDateTime;

/**
 * 공지사항 목록의 공지 한 건을 표현합니다.
 */
public record NoticeListItem(
        Long id,
        String title,
        String authorName,
        LocalDateTime createdAt
) {
    public static NoticeListItem from(Notice notice) {
        return new NoticeListItem(
                notice.getId(),
                notice.getTitle(),
                notice.getAuthor().getName(),
                notice.getCreatedAt()
        );
    }
}
