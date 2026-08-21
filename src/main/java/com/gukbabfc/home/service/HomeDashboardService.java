package com.gukbabfc.home.service;

import com.gukbabfc.freeboard.dao.FreeBoardCommentRepository;
import com.gukbabfc.freeboard.dao.FreeBoardRepository;
import com.gukbabfc.freeboard.dto.FreeBoardCommentCount;
import com.gukbabfc.freeboard.entity.FreeBoardPost;
import com.gukbabfc.home.dto.HomeDashboard;
import com.gukbabfc.home.dto.NextScheduleCard;
import com.gukbabfc.home.dto.OpenUniformOrderCard;
import com.gukbabfc.home.dto.RecentNoticeCard;
import com.gukbabfc.home.dto.RecentPostCard;
import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.entity.Member;
import com.gukbabfc.member.exception.MemberNotFoundException;
import com.gukbabfc.notice.dao.NoticeRepository;
import com.gukbabfc.schedule.dao.ScheduleParticipationRepository;
import com.gukbabfc.schedule.dao.ScheduleRepository;
import com.gukbabfc.schedule.entity.ParticipationStatus;
import com.gukbabfc.schedule.entity.Schedule;
import com.gukbabfc.uniform.dao.UniformOrderPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 여러 기능의 최신 데이터를 모아 로그인 회원용 메인 대시보드를 구성합니다.
 */
@Service
@RequiredArgsConstructor
public class HomeDashboardService {

    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleParticipationRepository scheduleParticipationRepository;
    private final NoticeRepository noticeRepository;
    private final FreeBoardRepository freeBoardRepository;
    private final FreeBoardCommentRepository freeBoardCommentRepository;
    private final UniformOrderPeriodRepository uniformOrderPeriodRepository;

    @Transactional(readOnly = true)
    public HomeDashboard getDashboard(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
        LocalDateTime now = LocalDateTime.now();

        return new HomeDashboard(
                findNextSchedule(member, now),
                findRecentNotices(),
                findRecentPosts(),
                findOpenUniformOrder(now)
        );
    }

    private NextScheduleCard findNextSchedule(Member member, LocalDateTime now) {
        return scheduleRepository.findFirstByScheduledAtGreaterThanEqualOrderByScheduledAtAsc(now)
                .map(schedule -> NextScheduleCard.from(schedule, findParticipationStatus(schedule, member)))
                .orElse(null);
    }

    private ParticipationStatus findParticipationStatus(Schedule schedule, Member member) {
        return scheduleParticipationRepository.findByScheduleIdAndMemberId(schedule.getId(), member.getId())
                .map(participation -> participation.getStatus())
                .orElse(null);
    }

    private List<RecentNoticeCard> findRecentNotices() {
        return noticeRepository.findTop3ByOrderByCreatedAtDesc().stream()
                .map(RecentNoticeCard::from)
                .toList();
    }

    private List<RecentPostCard> findRecentPosts() {
        List<FreeBoardPost> posts = freeBoardRepository.findTop3ByOrderByCreatedAtDesc();
        List<Long> postIds = posts.stream().map(FreeBoardPost::getId).toList();
        Map<Long, Long> commentCounts = postIds.isEmpty()
                ? Map.of()
                : freeBoardCommentRepository.countByPostIds(postIds).stream()
                .collect(Collectors.toMap(FreeBoardCommentCount::getPostId,
                        FreeBoardCommentCount::getCommentCount, (first, second) -> first));

        return posts.stream()
                .map(post -> RecentPostCard.from(post, commentCounts.getOrDefault(post.getId(), 0L)))
                .toList();
    }

    private OpenUniformOrderCard findOpenUniformOrder(LocalDateTime now) {
        return uniformOrderPeriodRepository
                .findFirstByClosedFalseAndStartsAtLessThanEqualAndEndsAtGreaterThanOrderByEndsAtAsc(now, now)
                .map(OpenUniformOrderCard::from)
                .orElse(null);
    }
}
