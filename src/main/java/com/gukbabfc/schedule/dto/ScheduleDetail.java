package com.gukbabfc.schedule.dto;

import com.gukbabfc.schedule.entity.Schedule;

import java.time.LocalDateTime;

public record ScheduleDetail(
        Long id,
        String title,
        String location,
        LocalDateTime scheduledAt,
        String description,
        String createdByName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ScheduleDetail from(Schedule schedule) {
        return new ScheduleDetail(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getLocation(),
                schedule.getScheduledAt(),
                schedule.getDescription(),
                schedule.getCreatedBy().getName(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }
}
