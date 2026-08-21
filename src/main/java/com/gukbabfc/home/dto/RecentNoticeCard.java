package com.gukbabfc.home.dto;

import com.gukbabfc.notice.entity.Notice;

import java.time.LocalDateTime;

/**
 * 대시보드의 최근 공지사항 한 건을 표현합니다.
 */
public record RecentNoticeCard(
        Long id,
        String title,
        LocalDateTime createdAt
) {
    public static RecentNoticeCard from(Notice notice) {
        return new RecentNoticeCard(notice.getId(), notice.getTitle(), notice.getCreatedAt());
    }
}
