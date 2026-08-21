package com.gukbabfc.home.dto;

import java.util.List;

/**
 * 메인 화면에 필요한 여러 기능의 요약 데이터를 한 번에 전달합니다.
 */
public record HomeDashboard(
        NextScheduleCard nextSchedule,
        List<RecentNoticeCard> recentNotices,
        List<RecentPostCard> recentPosts,
        OpenUniformOrderCard openUniformOrder
) {
}
