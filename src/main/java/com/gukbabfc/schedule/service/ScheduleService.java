package com.gukbabfc.schedule.service;

import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.entity.Member;
import com.gukbabfc.member.exception.MemberNotFoundException;
import com.gukbabfc.schedule.dao.ScheduleRepository;
import com.gukbabfc.schedule.dao.ScheduleParticipationRepository;
import com.gukbabfc.schedule.dto.ParticipantItem;
import com.gukbabfc.schedule.dto.ParticipationSummary;
import com.gukbabfc.schedule.dto.ScheduleCreateRequest;
import com.gukbabfc.schedule.dto.ScheduleDetail;
import com.gukbabfc.schedule.dto.ScheduleListItem;
import com.gukbabfc.schedule.dto.ScheduleListResponse;
import com.gukbabfc.schedule.dto.ScheduleUpdateRequest;
import com.gukbabfc.schedule.entity.Schedule;
import com.gukbabfc.schedule.entity.ParticipationStatus;
import com.gukbabfc.schedule.entity.ScheduleParticipation;
import com.gukbabfc.schedule.exception.ScheduleNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 풋살 일정 분류와 관리자 CRUD 업무 규칙을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleParticipationRepository participationRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public ScheduleListResponse getSchedules() {
        LocalDateTime now = LocalDateTime.now();
        var upcomingSchedules = scheduleRepository
                .findByScheduledAtGreaterThanEqualOrderByScheduledAtAsc(now).stream()
                .map(ScheduleListItem::from)
                .toList();
        var pastSchedules = scheduleRepository
                .findByScheduledAtBeforeOrderByScheduledAtDesc(now).stream()
                .map(ScheduleListItem::from)
                .toList();
        return new ScheduleListResponse(upcomingSchedules, pastSchedules);
    }

    @Transactional(readOnly = true)
    public ScheduleDetail getSchedule(Long id) {
        return ScheduleDetail.from(findSchedule(id));
    }

    @Transactional(readOnly = true)
    public ParticipationSummary getParticipationSummary(Long scheduleId, String username) {
        findSchedule(scheduleId);
        var participations = participationRepository.findAllWithMemberByScheduleId(scheduleId);
        ParticipationStatus myStatus = participations.stream()
                .filter(participation -> participation.getMember().getUsername().equals(username))
                .map(ScheduleParticipation::getStatus)
                .findFirst()
                .orElse(null);

        var attendingMembers = filterParticipants(participations, ParticipationStatus.ATTENDING);
        var notAttendingMembers = filterParticipants(participations, ParticipationStatus.NOT_ATTENDING);
        var undecidedMembers = filterParticipants(participations, ParticipationStatus.UNDECIDED);
        return new ParticipationSummary(
                myStatus,
                attendingMembers,
                notAttendingMembers,
                undecidedMembers
        );
    }

    @Transactional
    public void respondToSchedule(Long scheduleId, String username, ParticipationStatus status) {
        Schedule schedule = findSchedule(scheduleId);
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);

        participationRepository.findByScheduleIdAndMemberId(scheduleId, member.getId())
                .ifPresentOrElse(
                        participation -> participation.changeStatus(status),
                        () -> participationRepository.save(
                                new ScheduleParticipation(schedule, member, status)
                        )
                );
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public ScheduleUpdateRequest getUpdateRequest(Long id) {
        return ScheduleUpdateRequest.from(ScheduleDetail.from(findSchedule(id)));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Long createSchedule(String username, ScheduleCreateRequest request) {
        Member createdBy = memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
        Schedule schedule = new Schedule(
                request.getTitle().trim(),
                request.getLocation().trim(),
                request.getScheduledAt(),
                normalize(request.getDescription()),
                createdBy
        );
        return scheduleRepository.save(schedule).getId();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updateSchedule(Long id, ScheduleUpdateRequest request) {
        Schedule schedule = findSchedule(id);
        schedule.update(
                request.getTitle().trim(),
                request.getLocation().trim(),
                request.getScheduledAt(),
                normalize(request.getDescription())
        );
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSchedule(Long id) {
        Schedule schedule = findSchedule(id);
        participationRepository.deleteAllByScheduleId(id);
        scheduleRepository.delete(schedule);
    }

    private Schedule findSchedule(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(ScheduleNotFoundException::new);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private List<ParticipantItem> filterParticipants(
            List<ScheduleParticipation> participations,
            ParticipationStatus status
    ) {
        return participations.stream()
                .filter(participation -> participation.getStatus() == status)
                .map(ParticipantItem::from)
                .toList();
    }
}
