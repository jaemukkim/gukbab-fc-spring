package com.gukbabfc.notice;

import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.entity.Member;
import com.gukbabfc.notice.dao.NoticeRepository;
import com.gukbabfc.notice.dto.NoticeDetail;
import com.gukbabfc.notice.entity.Notice;
import com.gukbabfc.notice.service.NoticeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class NoticeFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member author;

    @BeforeEach
    void setUp() {
        noticeRepository.deleteAll();
        author = memberRepository.findByUsername("noticeuser")
                .orElseGet(() -> memberRepository.save(
                        new Member("noticeuser", passwordEncoder.encode("password1234"), "공지담당")
                ));
    }

    @Test
    void 로그인하지_않으면_공지사항에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/notices"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "noticeuser", roles = "MEMBER")
    void 공지사항_목록과_상세를_조회할_수_있다() throws Exception {
        Notice notice = noticeRepository.save(new Notice("정기 풋살 안내", "토요일에 만나요.", author));

        mockMvc.perform(get("/notices"))
                .andExpect(status().isOk())
                .andExpect(view().name("notice/list"))
                .andExpect(model().attributeExists("notices"));

        mockMvc.perform(get("/notices/{id}", notice.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("notice/detail"))
                .andExpect(model().attributeExists("notice"));

        NoticeDetail detail = noticeService.getNotice(notice.getId());
        assertThat(detail.title()).isEqualTo("정기 풋살 안내");
        assertThat(detail.content()).isEqualTo("토요일에 만나요.");
    }

    @Test
    @WithMockUser(username = "noticeuser", roles = "ADMIN")
    void 공지사항을_작성할_수_있다() throws Exception {
        mockMvc.perform(post("/notices")
                        .with(csrf())
                        .param("title", "  유니폼 신청 안내  ")
                        .param("content", "  이번 주까지 신청해 주세요.  "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/notices/*"));

        Notice notice = noticeRepository.findAll().getFirst();
        NoticeDetail detail = noticeService.getNotice(notice.getId());
        assertThat(detail.title()).isEqualTo("유니폼 신청 안내");
        assertThat(detail.content()).isEqualTo("이번 주까지 신청해 주세요.");
        assertThat(detail.authorName()).isEqualTo("공지담당");
    }

    @Test
    @WithMockUser(username = "noticeuser", roles = "ADMIN")
    void 제목과_내용이_비어_있으면_공지사항을_작성할_수_없다() throws Exception {
        mockMvc.perform(post("/notices")
                        .with(csrf())
                        .param("title", "")
                        .param("content", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("notice/form"))
                .andExpect(model().attributeHasFieldErrors("noticeCreateRequest", "title", "content"));

        assertThat(noticeRepository.count()).isZero();
    }

    @Test
    @WithMockUser(username = "noticeuser", roles = "MEMBER")
    void 일반_회원은_공지사항_작성_화면에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/notices/new"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    @WithMockUser(username = "noticeuser", roles = "MEMBER")
    void 일반_회원은_공지사항을_작성할_수_없다() throws Exception {
        mockMvc.perform(post("/notices")
                        .with(csrf())
                        .param("title", "권한 없는 공지")
                        .param("content", "등록되면 안 됩니다."))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));

        assertThat(noticeRepository.count()).isZero();
    }

    @Test
    @WithMockUser(username = "noticeuser", roles = "ADMIN")
    void 관리자는_공지사항을_수정할_수_있다() throws Exception {
        Notice notice = noticeRepository.save(new Notice("수정 전 제목", "수정 전 내용", author));

        mockMvc.perform(get("/notices/{id}/edit", notice.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("notice/edit-form"))
                .andExpect(model().attributeExists("noticeUpdateRequest"));

        mockMvc.perform(post("/notices/{id}/edit", notice.getId())
                        .with(csrf())
                        .param("title", "  수정된 제목  ")
                        .param("content", "  수정된 내용  "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notices/" + notice.getId() + "?updated"));

        NoticeDetail detail = noticeService.getNotice(notice.getId());
        assertThat(detail.title()).isEqualTo("수정된 제목");
        assertThat(detail.content()).isEqualTo("수정된 내용");
        assertThat(detail.updatedAt()).isNotNull();
    }

    @Test
    @WithMockUser(username = "noticeuser", roles = "ADMIN")
    void 관리자는_공지사항을_삭제할_수_있다() throws Exception {
        Notice notice = noticeRepository.save(new Notice("삭제할 공지", "삭제할 내용", author));

        mockMvc.perform(post("/notices/{id}/delete", notice.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notices?deleted"));

        assertThat(noticeRepository.existsById(notice.getId())).isFalse();
    }

    @Test
    @WithMockUser(username = "noticeuser", roles = "MEMBER")
    void 일반_회원은_공지사항을_수정하거나_삭제할_수_없다() throws Exception {
        Notice notice = noticeRepository.save(new Notice("원래 제목", "원래 내용", author));

        mockMvc.perform(post("/notices/{id}/edit", notice.getId())
                        .with(csrf())
                        .param("title", "권한 없는 수정")
                        .param("content", "수정되면 안 됩니다."))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));

        mockMvc.perform(post("/notices/{id}/delete", notice.getId()).with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));

        Notice savedNotice = noticeRepository.findById(notice.getId()).orElseThrow();
        assertThat(savedNotice.getTitle()).isEqualTo("원래 제목");
    }

    @Test
    @WithMockUser(username = "noticeuser", roles = "MEMBER")
    void 존재하지_않는_공지사항은_404_안내_화면을_보여준다() throws Exception {
        mockMvc.perform(get("/notices/999999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"))
                .andExpect(model().attributeExists("message"));
    }
}
