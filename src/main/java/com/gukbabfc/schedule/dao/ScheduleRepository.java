package com.gukbabfc.schedule.dao;

import com.gukbabfc.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 풋살 일정의 저장과 예정·지난 일정 조회를 담당합니다.
 */
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByScheduledAtGreaterThanEqualOrderByScheduledAtAsc(LocalDateTime dateTime);

    List<Schedule> findByScheduledAtBeforeOrderByScheduledAtDesc(LocalDateTime dateTime);
}
