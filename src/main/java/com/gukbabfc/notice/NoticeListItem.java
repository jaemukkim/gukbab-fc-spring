package com.gukbabfc.notice;

import java.time.LocalDateTime;

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
