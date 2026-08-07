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
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "noticeuser", roles = "MEMBER")
    void 일반_회원은_공지사항을_작성할_수_없다() throws Exception {
        mockMvc.perform(post("/notices")
                        .with(csrf())
                        .param("title", "권한 없는 공지")
                        .param("content", "등록되면 안 됩니다."))
                .andExpect(status().isForbidden());

        assertThat(noticeRepository.count()).isZero();
    }
}
