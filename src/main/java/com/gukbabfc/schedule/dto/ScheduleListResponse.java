package com.gukbabfc.schedule.dto;

import java.util.List;

public record ScheduleListResponse(
        List<ScheduleListItem> upcomingSchedules,
        List<ScheduleListItem> pastSchedules
) {
}
