package com.gukbabfc.uniform;

import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.entity.Member;
import com.gukbabfc.uniform.dao.UniformOrderPeriodRepository;
import com.gukbabfc.uniform.dao.UniformApplicationRepository;
import com.gukbabfc.uniform.dto.UniformApplicationSummary;
import com.gukbabfc.uniform.dto.UniformOrderPeriodView;
import com.gukbabfc.uniform.entity.UniformOrderPeriod;
import com.gukbabfc.uniform.entity.UniformOrderStatus;
import com.gukbabfc.uniform.entity.UniformSize;
import com.gukbabfc.uniform.dto.UniformApplicationRequest;
import com.gukbabfc.uniform.service.UniformApplicationService;
import com.gukbabfc.uniform.service.UniformOrderPeriodService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * 유니폼 신청 기간 조회, 관리자 등록·수정·마감 권한을 검증합니다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UniformOrderPeriodFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UniformOrderPeriodRepository periodRepository;

    @Autowired
    private UniformApplicationRepository applicationRepository;

    @Autowired
    private UniformOrderPeriodService periodService;

    @Autowired
    private UniformApplicationService applicationService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member manager;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        periodRepository.deleteAll();
        manager = memberRepository.findByUsername("uniformmanager")
                .orElseGet(() -> memberRepository.save(
                        new Member("uniformmanager", passwordEncoder.encode("password1234"), "유니폼담당")
                ));
    }

    @Test
    void 로그인하지_않으면_유니폼_신청_기간을_조회할_수_없다() throws Exception {
        mockMvc.perform(get("/uniform-orders"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "uniformmanager", roles = "MEMBER")
    void 회원은_유니폼_신청_기간_목록과_상세를_조회할_수_있다() throws Exception {
        UniformOrderPeriod period = saveOpenPeriod("홈 유니폼 신청");

        mockMvc.perform(get("/uniform-orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("uniform/list"))
                .andExpect(model().attributeExists("periods"));

        mockMvc.perform(get("/uniform-orders/{id}", period.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("uniform/detail"))
                .andExpect(model().attributeExists(
                        "period", "uniformApplicationRequest", "uniformSizes"
                ));

        assertThat(periodService.getPeriod(period.getId()).status())
                .isEqualTo(UniformOrderStatus.OPEN);
    }

    @Test
    void 현재_시각과_수동_마감_여부로_신청_상태를_계산한다() {
        UniformOrderPeriod upcoming = periodRepository.save(new UniformOrderPeriod(
                "신청 전", null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), manager
        ));
        UniformOrderPeriod closed = saveOpenPeriod("수동 마감");
        closed.close();
        periodRepository.save(closed);

        assertThat(periodService.getPeriod(upcoming.getId()).status())
                .isEqualTo(UniformOrderStatus.UPCOMING);
        assertThat(periodService.getPeriod(closed.getId()).status())
                .isEqualTo(UniformOrderStatus.CLOSED);
    }

    @Test
    @WithMockUser(username = "uniformmanager", roles = "ADMIN")
    void 관리자는_유니폼_신청_기간을_등록할_수_있다() throws Exception {
        mockMvc.perform(post("/uniform-orders")
                        .with(csrf())
                        .param("title", "  2026 원정 유니폼  ")
                        .param("startsAt", "2026-08-13T10:00")
                        .param("endsAt", "2026-08-20T22:00")
                        .param("description", "  사이즈표를 확인해 주세요.  "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/uniform-orders/*"));

        UniformOrderPeriod saved = periodRepository.findAll().getFirst();
        UniformOrderPeriodView detail = periodService.getPeriod(saved.getId());
        assertThat(detail.title()).isEqualTo("2026 원정 유니폼");
        assertThat(detail.description()).isEqualTo("사이즈표를 확인해 주세요.");
        assertThat(detail.createdByName()).isEqualTo("유니폼담당");
    }

    @Test
    @WithMockUser(username = "uniformmanager", roles = "ADMIN")
    void 마감_시각이_시작보다_빠르면_신청_기간을_등록할_수_없다() throws Exception {
        mockMvc.perform(post("/uniform-orders")
                        .with(csrf())
                        .param("title", "잘못된 기간")
                        .param("startsAt", "2026-08-20T22:00")
                        .param("endsAt", "2026-08-13T10:00"))
                .andExpect(status().isOk())
                .andExpect(view().name("uniform/form"))
                .andExpect(model().attributeHasFieldErrors(
                        "uniformOrderPeriodRequest", "periodValid"
                ));

        assertThat(periodRepository.count()).isZero();
    }

    @Test
    @WithMockUser(username = "uniformmanager", roles = "ADMIN")
    void 관리자는_신청_기간을_수정하고_마감하고_재오픈할_수_있다() throws Exception {
        UniformOrderPeriod period = saveOpenPeriod("수정 전 신청");

        mockMvc.perform(post("/uniform-orders/{id}/edit", period.getId())
                        .with(csrf())
                        .param("title", "수정된 신청")
                        .param("startsAt", "2026-08-01T10:00")
                        .param("endsAt", "2026-09-01T22:00")
                        .param("description", "수정된 안내"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uniform-orders/" + period.getId() + "?updated"));

        mockMvc.perform(post("/uniform-orders/{id}/close", period.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uniform-orders/" + period.getId() + "?closed"));
        assertThat(periodService.getPeriod(period.getId()).status())
                .isEqualTo(UniformOrderStatus.CLOSED);

        mockMvc.perform(post("/uniform-orders/{id}/reopen", period.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uniform-orders/" + period.getId() + "?reopened"));
        UniformOrderPeriodView reopened = periodService.getPeriod(period.getId());
        assertThat(reopened.title()).isEqualTo("수정된 신청");
        assertThat(reopened.status()).isEqualTo(UniformOrderStatus.OPEN);
    }

    @Test
    @WithMockUser(username = "uniformmanager", roles = "MEMBER")
    void 일반_회원은_신청_기간을_등록하거나_마감할_수_없다() throws Exception {
        UniformOrderPeriod period = saveOpenPeriod("관리자 신청");

        mockMvc.perform(post("/uniform-orders")
                        .with(csrf())
                        .param("title", "권한 없는 신청")
                        .param("startsAt", "2026-08-13T10:00")
                        .param("endsAt", "2026-08-20T22:00"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));

        mockMvc.perform(post("/uniform-orders/{id}/close", period.getId()).with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));

        assertThat(periodService.getPeriod(period.getId()).status())
                .isEqualTo(UniformOrderStatus.OPEN);
    }

    @Test
    @WithMockUser(username = "uniformmanager", roles = "MEMBER")
    void 존재하지_않는_신청_기간은_404_화면을_보여준다() throws Exception {
        mockMvc.perform(get("/uniform-orders/999999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    @WithMockUser(username = "uniformmanager", roles = "MEMBER")
    void 회원은_유니폼을_신청하고_기존_신청을_수정할_수_있다() throws Exception {
        UniformOrderPeriod period = saveOpenPeriod("회원 신청");

        mockMvc.perform(post("/uniform-orders/{id}/application", period.getId())
                        .with(csrf())
                        .param("size", "L")
                        .param("backNumber", "10")
                        .param("markingName", "  KIM  ")
                        .param("quantity", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/uniform-orders/" + period.getId() + "?applicationSaved"
                ));

        assertThat(applicationRepository.count()).isEqualTo(1);
        assertThat(applicationRepository.findAll().getFirst().getMarkingName()).isEqualTo("KIM");

        mockMvc.perform(post("/uniform-orders/{id}/application", period.getId())
                        .with(csrf())
                        .param("size", "XL")
                        .param("backNumber", "7")
                        .param("markingName", "PARK")
                        .param("quantity", "2"))
                .andExpect(status().is3xxRedirection());

        assertThat(applicationRepository.count()).isEqualTo(1);
        assertThat(applicationRepository.findAll().getFirst().getSize()).isEqualTo(UniformSize.XL);
        assertThat(applicationRepository.findAll().getFirst().getQuantity()).isEqualTo(2);
        assertThat(applicationService.getMyApplication(period.getId(), manager.getUsername()))
                .isPresent();
    }

    @Test
    @WithMockUser(username = "uniformmanager", roles = "MEMBER")
    void 회원은_신청_기간_중에_자신의_신청을_취소할_수_있다() throws Exception {
        UniformOrderPeriod period = saveOpenPeriod("취소 신청");
        apply(period, manager, UniformSize.M, 9, "KIM", 1);

        mockMvc.perform(post("/uniform-orders/{id}/application/cancel", period.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/uniform-orders/" + period.getId() + "?applicationCancelled"
                ));

        assertThat(applicationRepository.count()).isZero();
    }

    @Test
    @WithMockUser(username = "uniformmanager", roles = "MEMBER")
    void 잘못된_유니폼_신청값은_상세_화면에서_검증_오류를_보여준다() throws Exception {
        UniformOrderPeriod period = saveOpenPeriod("입력 검증 신청");

        mockMvc.perform(post("/uniform-orders/{id}/application", period.getId())
                        .with(csrf())
                        .param("size", "")
                        .param("backNumber", "100")
                        .param("markingName", "")
                        .param("quantity", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("uniform/detail"))
                .andExpect(model().attributeHasFieldErrors(
                        "uniformApplicationRequest", "size", "backNumber", "markingName", "quantity"
                ));

        assertThat(applicationRepository.count()).isZero();
    }

    @Test
    @WithMockUser(username = "uniformmanager", roles = "MEMBER")
    void 마감된_기간에는_직접_요청해도_신청할_수_없다() throws Exception {
        UniformOrderPeriod period = saveOpenPeriod("마감 신청");
        period.close();
        periodRepository.save(period);

        mockMvc.perform(post("/uniform-orders/{id}/application", period.getId())
                        .with(csrf())
                        .param("size", "L")
                        .param("backNumber", "10")
                        .param("markingName", "KIM")
                        .param("quantity", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uniform-orders/" + period.getId()))
                .andExpect(flash().attributeExists("applicationError"));

        assertThat(applicationRepository.count()).isZero();
    }

    @Test
    @WithMockUser(username = "uniformmanager", roles = "ADMIN")
    void 관리자는_전체_신청과_사이즈별_수량을_집계해서_조회한다() throws Exception {
        UniformOrderPeriod period = saveOpenPeriod("관리자 집계");
        Member secondMember = findOrCreateMember("uniformmember", "두번째회원");
        apply(period, manager, UniformSize.L, 10, "KIM", 2);
        apply(period, secondMember, UniformSize.L, 20, "LEE", 1);

        UniformApplicationSummary summary = applicationService.getSummary(period.getId());
        assertThat(summary.applications()).hasSize(2);
        assertThat(summary.quantityBySize().get(UniformSize.L)).isEqualTo(3);
        assertThat(summary.quantityBySize().get(UniformSize.XL)).isZero();
        assertThat(summary.totalQuantity()).isEqualTo(3);

        mockMvc.perform(get("/uniform-orders/{id}", period.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("uniform/detail"))
                .andExpect(model().attributeExists("applicationSummary"));
    }

    private UniformOrderPeriod saveOpenPeriod(String title) {
        return periodRepository.save(new UniformOrderPeriod(
                title,
                "테스트 신청 안내입니다.",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(7),
                manager
        ));
    }

    private void apply(UniformOrderPeriod period, Member member, UniformSize size,
                       int backNumber, String markingName, int quantity) {
        UniformApplicationRequest request = new UniformApplicationRequest();
        request.setSize(size);
        request.setBackNumber(backNumber);
        request.setMarkingName(markingName);
        request.setQuantity(quantity);
        applicationService.apply(period.getId(), member.getUsername(), request);
    }

    private Member findOrCreateMember(String username, String name) {
        return memberRepository.findByUsername(username)
                .orElseGet(() -> memberRepository.save(
                        new Member(username, passwordEncoder.encode("password1234"), name)
                ));
    }
}
