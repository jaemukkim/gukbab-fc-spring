package com.gukbabfc.schedule.dao;

import com.gukbabfc.schedule.entity.ScheduleParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 일정별 참가 응답의 저장, 회원별 조회, 일괄 삭제를 담당합니다.
 */
public interface ScheduleParticipationRepository extends JpaRepository<ScheduleParticipation, Long> {

    Optional<ScheduleParticipation> findByScheduleIdAndMemberId(Long scheduleId, Long memberId);

    @Query("""
            select participation
            from ScheduleParticipation participation
            join fetch participation.member
            where participation.schedule.id = :scheduleId
            order by participation.respondedAt asc, participation.id asc
            """)
    List<ScheduleParticipation> findAllWithMemberByScheduleId(@Param("scheduleId") Long scheduleId);

    @Modifying
    @Query("delete from ScheduleParticipation participation where participation.schedule.id = :scheduleId")
    void deleteAllByScheduleId(@Param("scheduleId") Long scheduleId);
}
