package com.gukbabfc.schedule.dao;

import com.gukbabfc.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByScheduledAtGreaterThanEqualOrderByScheduledAtAsc(LocalDateTime dateTime);

    List<Schedule> findByScheduledAtBeforeOrderByScheduledAtDesc(LocalDateTime dateTime);
}
