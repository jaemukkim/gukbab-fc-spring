package com.gukbabfc.freeboard;

import com.gukbabfc.freeboard.dao.FreeBoardRepository;
import com.gukbabfc.freeboard.dto.FreeBoardDetail;
import com.gukbabfc.freeboard.entity.FreeBoardPost;
import com.gukbabfc.freeboard.service.FreeBoardService;
import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.entity.Member;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class FreeBoardFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FreeBoardRepository freeBoardRepository;

    @Autowired
    private FreeBoardService freeBoardService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member author;
    private Member otherMember;
    private Member admin;

    @BeforeEach
    void setUp() {
        freeBoardRepository.deleteAll();
        author = findOrCreateMember("freeauthor", "작성자");
        otherMember = findOrCreateMember("othermember", "다른회원");
        admin = findOrCreateMember("freeadmin", "관리자");
        admin.promoteToAdmin();
        memberRepository.save(admin);
    }

    @Test
    void 로그인하지_않으면_자유게시판에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/freeboards"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "freeauthor", roles = "MEMBER")
    void 게시글을_작성하고_조회할_수_있다() throws Exception {
        mockMvc.perform(post("/freeboards")
                        .with(csrf())
                        .param("title", "  첫 자유게시글  ")
                        .param("content", "  반갑습니다.  "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/freeboards/*"));

        FreeBoardPost saved = freeBoardRepository.findAll().getFirst();
        mockMvc.perform(get("/freeboards/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("freeboard/detail"))
                .andExpect(model().attribute("canManage", true));

        FreeBoardDetail detail = freeBoardService.getPost(saved.getId());
        assertThat(detail.title()).isEqualTo("첫 자유게시글");
        assertThat(detail.content()).isEqualTo("반갑습니다.");
        assertThat(detail.authorUsername()).isEqualTo("freeauthor");
    }

    @Test
    @WithMockUser(username = "freeauthor", roles = "MEMBER")
    void 빈_제목과_내용으로는_게시글을_작성할_수_없다() throws Exception {
        mockMvc.perform(post("/freeboards")
                        .with(csrf())
                        .param("title", "")
                        .param("content", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("freeboard/form"))
                .andExpect(model().attributeHasFieldErrors("freeBoardCreateRequest", "title", "content"));

        assertThat(freeBoardRepository.count()).isZero();
    }

    @Test
    @WithMockUser(username = "freeauthor", roles = "MEMBER")
    void 작성자는_자신의_게시글을_수정할_수_있다() throws Exception {
        FreeBoardPost post = savePost("수정 전", "수정 전 내용");

        mockMvc.perform(get("/freeboards/{id}/edit", post.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("freeboard/edit-form"));

        mockMvc.perform(post("/freeboards/{id}/edit", post.getId())
                        .with(csrf())
                        .param("title", "수정 후")
                        .param("content", "수정 후 내용"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/freeboards/" + post.getId() + "?updated"));

        FreeBoardDetail detail = freeBoardService.getPost(post.getId());
        assertThat(detail.title()).isEqualTo("수정 후");
        assertThat(detail.updatedAt()).isNotNull();
    }

    @Test
    @WithMockUser(username = "freeauthor", roles = "MEMBER")
    void 작성자는_자신의_게시글을_삭제할_수_있다() throws Exception {
        FreeBoardPost post = savePost("삭제할 글", "삭제할 내용");

        mockMvc.perform(post("/freeboards/{id}/delete", post.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/freeboards?deleted"));

        assertThat(freeBoardRepository.existsById(post.getId())).isFalse();
    }

    @Test
    @WithMockUser(username = "othermember", roles = "MEMBER")
    void 다른_회원의_게시글은_수정하거나_삭제할_수_없다() throws Exception {
        FreeBoardPost post = savePost("작성자 글", "작성자 내용");

        mockMvc.perform(post("/freeboards/{id}/edit", post.getId())
                        .with(csrf())
                        .param("title", "권한 없는 수정")
                        .param("content", "수정되면 안 됩니다."))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));

        mockMvc.perform(post("/freeboards/{id}/delete", post.getId()).with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));

        assertThat(freeBoardRepository.findById(post.getId()).orElseThrow().getTitle()).isEqualTo("작성자 글");
    }

    @Test
    @WithMockUser(username = "freeadmin", roles = "ADMIN")
    void 관리자는_다른_회원의_게시글을_수정하고_삭제할_수_있다() throws Exception {
        FreeBoardPost post = savePost("관리 대상", "관리 대상 내용");

        mockMvc.perform(post("/freeboards/{id}/edit", post.getId())
                        .with(csrf())
                        .param("title", "관리자 수정")
                        .param("content", "관리자가 수정했습니다."))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/freeboards/{id}/delete", post.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection());

        assertThat(freeBoardRepository.existsById(post.getId())).isFalse();
    }

    @Test
    @WithMockUser(username = "freeauthor", roles = "MEMBER")
    void 존재하지_않는_게시글은_404_안내_화면을_보여준다() throws Exception {
        mockMvc.perform(get("/freeboards/999999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"))
                .andExpect(model().attributeExists("message"));
    }

    private Member findOrCreateMember(String username, String name) {
        return memberRepository.findByUsername(username)
                .orElseGet(() -> memberRepository.save(
                        new Member(username, passwordEncoder.encode("password1234"), name)
                ));
    }

    private FreeBoardPost savePost(String title, String content) {
        return freeBoardRepository.save(new FreeBoardPost(title, content, author));
    }
}
