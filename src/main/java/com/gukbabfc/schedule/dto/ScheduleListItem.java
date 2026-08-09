package com.gukbabfc.schedule.dto;

import com.gukbabfc.schedule.entity.Schedule;

import java.time.LocalDateTime;

/**
 * 풋살 일정 목록의 일정 한 건을 표현합니다.
 */
public record ScheduleListItem(
        Long id,
        String title,
        String location,
        LocalDateTime scheduledAt
) {
    public static ScheduleListItem from(Schedule schedule) {
        return new ScheduleListItem(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getLocation(),
                schedule.getScheduledAt()
        );
    }
}
