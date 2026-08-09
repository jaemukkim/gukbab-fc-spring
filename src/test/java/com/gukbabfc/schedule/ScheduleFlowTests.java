package com.gukbabfc.schedule;

import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.entity.Member;
import com.gukbabfc.schedule.dao.ScheduleRepository;
import com.gukbabfc.schedule.dto.ScheduleDetail;
import com.gukbabfc.schedule.dto.ScheduleListResponse;
import com.gukbabfc.schedule.entity.Schedule;
import com.gukbabfc.schedule.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * 풋살 일정 CRUD, 분류, 관리자 권한을 검증하는 통합 테스트입니다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ScheduleFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member manager;

    @BeforeEach
    void setUp() {
        scheduleRepository.deleteAll();
        manager = memberRepository.findByUsername("schedulemanager")
                .orElseGet(() -> memberRepository.save(
                        new Member("schedulemanager", passwordEncoder.encode("password1234"), "일정담당")
                ));
    }

    @Test
    void 로그인하지_않으면_풋살_일정에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/schedules"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "schedulemanager", roles = "MEMBER")
    void 회원은_풋살_일정_목록과_상세를_조회할_수_있다() throws Exception {
        Schedule schedule = saveSchedule("토요일 정기 풋살", LocalDateTime.now().plusDays(3));

        mockMvc.perform(get("/schedules"))
                .andExpect(status().isOk())
                .andExpect(view().name("schedule/list"))
                .andExpect(model().attributeExists("upcomingSchedules", "pastSchedules"));

        mockMvc.perform(get("/schedules/{id}", schedule.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("schedule/detail"))
                .andExpect(model().attributeExists("schedule"));
    }

    @Test
    void 예정_일정과_지난_일정을_시간순으로_나누어_조회한다() {
        saveSchedule("먼 예정 일정", LocalDateTime.now().plusDays(10));
        saveSchedule("가까운 예정 일정", LocalDateTime.now().plusDays(2));
        saveSchedule("오래된 지난 일정", LocalDateTime.now().minusDays(10));
        saveSchedule("최근 지난 일정", LocalDateTime.now().minusDays(1));

        ScheduleListResponse schedules = scheduleService.getSchedules();

        assertThat(schedules.upcomingSchedules())
                .extracting(item -> item.title())
                .containsExactly("가까운 예정 일정", "먼 예정 일정");
        assertThat(schedules.pastSchedules())
                .extracting(item -> item.title())
                .containsExactly("최근 지난 일정", "오래된 지난 일정");
    }

    @Test
    @WithMockUser(username = "schedulemanager", roles = "ADMIN")
    void 관리자는_풋살_일정을_등록할_수_있다() throws Exception {
        mockMvc.perform(post("/schedules")
                        .with(csrf())
                        .param("title", "  일요일 풋살  ")
                        .param("location", "  한강 풋살장  ")
                        .param("scheduledAt", "2026-09-13T18:30")
                        .param("description", "  운동화와 물을 준비해 주세요.  "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/schedules/*"));

        Schedule saved = scheduleRepository.findAll().getFirst();
        ScheduleDetail detail = scheduleService.getSchedule(saved.getId());
        assertThat(detail.title()).isEqualTo("일요일 풋살");
        assertThat(detail.location()).isEqualTo("한강 풋살장");
        assertThat(detail.description()).isEqualTo("운동화와 물을 준비해 주세요.");
        assertThat(detail.createdByName()).isEqualTo("일정담당");
    }

    @Test
    @WithMockUser(username = "schedulemanager", roles = "ADMIN")
    void 필수값이_비어_있으면_풋살_일정을_등록할_수_없다() throws Exception {
        mockMvc.perform(post("/schedules")
                        .with(csrf())
                        .param("title", "")
                        .param("location", "")
                        .param("scheduledAt", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("schedule/form"))
                .andExpect(model().attributeHasFieldErrors(
                        "scheduleCreateRequest", "title", "location", "scheduledAt"));

        assertThat(scheduleRepository.count()).isZero();
    }

    @Test
    @WithMockUser(username = "schedulemanager", roles = "MEMBER")
    void 일반_회원은_풋살_일정을_등록하거나_수정하거나_삭제할_수_없다() throws Exception {
        Schedule schedule = saveSchedule("관리자 일정", LocalDateTime.now().plusDays(3));

        mockMvc.perform(post("/schedules")
                        .with(csrf())
                        .param("title", "권한 없는 일정")
                        .param("location", "풋살장")
                        .param("scheduledAt", "2026-09-13T18:30"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));

        mockMvc.perform(post("/schedules/{id}/edit", schedule.getId())
                        .with(csrf())
                        .param("title", "권한 없는 수정")
                        .param("location", "다른 장소")
                        .param("scheduledAt", "2026-09-14T18:30"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));

        mockMvc.perform(post("/schedules/{id}/delete", schedule.getId()).with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));

        assertThat(scheduleRepository.count()).isEqualTo(1);
        assertThat(scheduleRepository.findById(schedule.getId()).orElseThrow().getTitle())
                .isEqualTo("관리자 일정");
    }

    @Test
    @WithMockUser(username = "schedulemanager", roles = "ADMIN")
    void 관리자는_풋살_일정을_수정하고_삭제할_수_있다() throws Exception {
        Schedule schedule = saveSchedule("수정 전 일정", LocalDateTime.now().plusDays(3));

        mockMvc.perform(get("/schedules/{id}/edit", schedule.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("schedule/edit-form"));

        mockMvc.perform(post("/schedules/{id}/edit", schedule.getId())
                        .with(csrf())
                        .param("title", "수정된 일정")
                        .param("location", "수정된 장소")
                        .param("scheduledAt", "2026-09-20T19:00")
                        .param("description", "수정된 안내"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedules/" + schedule.getId() + "?updated"));

        assertThat(scheduleService.getSchedule(schedule.getId()).title()).isEqualTo("수정된 일정");
        assertThat(scheduleService.getSchedule(schedule.getId()).updatedAt()).isNotNull();

        mockMvc.perform(post("/schedules/{id}/delete", schedule.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedules?deleted"));

        assertThat(scheduleRepository.existsById(schedule.getId())).isFalse();
    }

    @Test
    @WithMockUser(username = "schedulemanager", roles = "MEMBER")
    void 존재하지_않는_풋살_일정은_404_안내_화면을_보여준다() throws Exception {
        mockMvc.perform(get("/schedules/999999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"))
                .andExpect(model().attributeExists("message"));
    }

    private Schedule saveSchedule(String title, LocalDateTime scheduledAt) {
        return scheduleRepository.save(new Schedule(
                title,
                "테스트 풋살장",
                scheduledAt,
                "테스트 일정입니다.",
                manager
        ));
    }
}
