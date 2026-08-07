package com.gukbabfc.notice;

import java.time.LocalDateTime;

public record NoticeDetail(
        Long id,
        String title,
        String content,
        String authorName,
        LocalDateTime createdAt
) {
    public static NoticeDetail from(Notice notice) {
        return new NoticeDetail(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getAuthor().getName(),
                notice.getCreatedAt()
        );
    }
}
