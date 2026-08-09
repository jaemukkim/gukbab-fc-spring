package com.gukbabfc.schedule.dto;

import com.gukbabfc.schedule.entity.Schedule;

import java.time.LocalDateTime;

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
