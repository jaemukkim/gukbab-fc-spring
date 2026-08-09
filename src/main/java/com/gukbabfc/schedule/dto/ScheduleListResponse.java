package com.gukbabfc.schedule.dto;

import java.util.List;

/**
 * 예정 일정과 지난 일정 목록을 묶어서 전달합니다.
 */
public record ScheduleListResponse(
        List<ScheduleListItem> upcomingSchedules,
        List<ScheduleListItem> pastSchedules
) {
}
