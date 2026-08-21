package com.gukbabfc.home;

import com.gukbabfc.freeboard.dao.FreeBoardCommentRepository;
import com.gukbabfc.freeboard.dao.FreeBoardRepository;
import com.gukbabfc.freeboard.dto.FreeBoardCommentCount;
import com.gukbabfc.freeboard.entity.FreeBoardPost;
import com.gukbabfc.home.dto.HomeDashboard;
import com.gukbabfc.home.service.HomeDashboardService;
import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.entity.Member;
import com.gukbabfc.notice.dao.NoticeRepository;
import com.gukbabfc.notice.entity.Notice;
import com.gukbabfc.schedule.dao.ScheduleParticipationRepository;
import com.gukbabfc.schedule.dao.ScheduleRepository;
import com.gukbabfc.schedule.entity.ParticipationStatus;
import com.gukbabfc.schedule.entity.Schedule;
import com.gukbabfc.schedule.entity.ScheduleParticipation;
import com.gukbabfc.uniform.dao.UniformOrderPeriodRepository;
import com.gukbabfc.uniform.entity.UniformOrderPeriod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * 여러 저장소의 데이터가 메인 대시보드 요약으로 올바르게 조합되는지 확인합니다.
 */
@ExtendWith(MockitoExtension.class)
class HomeDashboardServiceTests {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private ScheduleParticipationRepository scheduleParticipationRepository;
    @Mock
    private NoticeRepository noticeRepository;
    @Mock
    private FreeBoardRepository freeBoardRepository;
    @Mock
    private FreeBoardCommentRepository freeBoardCommentRepository;
    @Mock
    private UniformOrderPeriodRepository uniformOrderPeriodRepository;
    @InjectMocks
    private HomeDashboardService homeDashboardService;

    @Test
    void 로그인회원의대시보드데이터를조합한다() {
        Member member = org.mockito.Mockito.mock(Member.class);
        Member author = org.mockito.Mockito.mock(Member.class);
        Schedule schedule = org.mockito.Mockito.mock(Schedule.class);
        ScheduleParticipation participation = org.mockito.Mockito.mock(ScheduleParticipation.class);
        Notice notice = org.mockito.Mockito.mock(Notice.class);
        FreeBoardPost post = org.mockito.Mockito.mock(FreeBoardPost.class);
        FreeBoardCommentCount commentCount = org.mockito.Mockito.mock(FreeBoardCommentCount.class);
        UniformOrderPeriod uniformOrder = org.mockito.Mockito.mock(UniformOrderPeriod.class);

        given(memberRepository.findByUsername("player1")).willReturn(Optional.of(member));
        given(schedule.getId()).willReturn(10L);
        given(schedule.getTitle()).willReturn("주말 풋살");
        given(schedule.getLocation()).willReturn("시민구장");
        given(schedule.getScheduledAt()).willReturn(LocalDateTime.now().plusDays(2));
        given(scheduleRepository.findFirstByScheduledAtGreaterThanEqualOrderByScheduledAtAsc(any()))
                .willReturn(Optional.of(schedule));
        given(scheduleParticipationRepository.findByScheduleIdAndMemberId(10L, member.getId()))
                .willReturn(Optional.of(participation));
        given(participation.getStatus()).willReturn(ParticipationStatus.ATTENDING);

        given(notice.getId()).willReturn(20L);
        given(notice.getTitle()).willReturn("새 공지");
        given(notice.getCreatedAt()).willReturn(LocalDateTime.now());
        given(noticeRepository.findTop3ByOrderByCreatedAtDesc()).willReturn(List.of(notice));

        given(post.getId()).willReturn(30L);
        given(post.getTitle()).willReturn("새 게시글");
        given(post.getAuthor()).willReturn(author);
        given(author.getName()).willReturn("작성자");
        given(post.getCreatedAt()).willReturn(LocalDateTime.now());
        given(freeBoardRepository.findTop3ByOrderByCreatedAtDesc()).willReturn(List.of(post));
        given(commentCount.getPostId()).willReturn(30L);
        given(commentCount.getCommentCount()).willReturn(2L);
        given(freeBoardCommentRepository.countByPostIds(List.of(30L))).willReturn(List.of(commentCount));

        given(uniformOrder.getId()).willReturn(40L);
        given(uniformOrder.getTitle()).willReturn("여름 유니폼 신청");
        given(uniformOrder.getEndsAt()).willReturn(LocalDateTime.now().plusDays(5));
        given(uniformOrderPeriodRepository
                .findFirstByClosedFalseAndStartsAtLessThanEqualAndEndsAtGreaterThanOrderByEndsAtAsc(any(), any()))
                .willReturn(Optional.of(uniformOrder));

        HomeDashboard dashboard = homeDashboardService.getDashboard("player1");

        assertThat(dashboard.nextSchedule().title()).isEqualTo("주말 풋살");
        assertThat(dashboard.nextSchedule().participationStatus()).isEqualTo(ParticipationStatus.ATTENDING);
        assertThat(dashboard.recentNotices()).extracting("title").containsExactly("새 공지");
        assertThat(dashboard.recentPosts()).singleElement().satisfies(recentPost -> {
            assertThat(recentPost.title()).isEqualTo("새 게시글");
            assertThat(recentPost.commentCount()).isEqualTo(2L);
        });
        assertThat(dashboard.openUniformOrder().title()).isEqualTo("여름 유니폼 신청");
    }

    @Test
    void 표시할데이터가없으면빈대시보드를반환한다() {
        Member member = org.mockito.Mockito.mock(Member.class);
        given(memberRepository.findByUsername("player1")).willReturn(Optional.of(member));
        given(scheduleRepository.findFirstByScheduledAtGreaterThanEqualOrderByScheduledAtAsc(any()))
                .willReturn(Optional.empty());
        given(noticeRepository.findTop3ByOrderByCreatedAtDesc()).willReturn(List.of());
        given(freeBoardRepository.findTop3ByOrderByCreatedAtDesc()).willReturn(List.of());
        given(uniformOrderPeriodRepository
                .findFirstByClosedFalseAndStartsAtLessThanEqualAndEndsAtGreaterThanOrderByEndsAtAsc(any(), any()))
                .willReturn(Optional.empty());

        HomeDashboard dashboard = homeDashboardService.getDashboard("player1");

        assertThat(dashboard.nextSchedule()).isNull();
        assertThat(dashboard.recentNotices()).isEmpty();
        assertThat(dashboard.recentPosts()).isEmpty();
        assertThat(dashboard.openUniformOrder()).isNull();
    }
}
