package com.gukbabfc.schedule.entity;

import com.gukbabfc.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 일정별 회원의 참가 상태와 마지막 응답 시각을 저장하는 연결 엔티티입니다.
 */
@Entity
@Table(
        name = "schedule_participations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_schedule_participation_schedule_member",
                columnNames = {"schedule_id", "member_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ParticipationStatus status;

    @Column(nullable = false)
    private LocalDateTime respondedAt;

    public ScheduleParticipation(Schedule schedule, Member member, ParticipationStatus status) {
        this.schedule = schedule;
        this.member = member;
        this.status = status;
        this.respondedAt = LocalDateTime.now();
    }

    public void changeStatus(ParticipationStatus status) {
        this.status = status;
        this.respondedAt = LocalDateTime.now();
    }
}
