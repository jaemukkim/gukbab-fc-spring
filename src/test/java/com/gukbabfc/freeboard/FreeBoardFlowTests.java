package com.gukbabfc.freeboard;

import com.gukbabfc.freeboard.dao.FreeBoardRepository;
import com.gukbabfc.freeboard.dao.FreeBoardCommentRepository;
import com.gukbabfc.freeboard.dto.FreeBoardDetail;
import com.gukbabfc.freeboard.dto.FreeBoardListItem;
import com.gukbabfc.freeboard.entity.FreeBoardPost;
import com.gukbabfc.freeboard.entity.FreeBoardComment;
import com.gukbabfc.freeboard.service.FreeBoardService;
import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
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

/**
 * 자유게시판 CRUD, 검색, 페이징, 작성자 권한을 검증합니다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class FreeBoardFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FreeBoardRepository freeBoardRepository;

    @Autowired
    private FreeBoardCommentRepository commentRepository;

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
        commentRepository.deleteAll();
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
        saveComment(post, author, "함께 삭제할 댓글");

        mockMvc.perform(post("/freeboards/{id}/delete", post.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/freeboards?deleted"));

        assertThat(freeBoardRepository.existsById(post.getId())).isFalse();
        assertThat(commentRepository.count()).isZero();
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

    @Test
    void 게시글을_한_페이지에_10개씩_최신순으로_조회한다() {
        for (int number = 1; number <= 12; number++) {
            savePost("게시글 " + number, "내용 " + number);
        }

        Page<FreeBoardListItem> firstPage = freeBoardService.getPosts(0, "");
        Page<FreeBoardListItem> secondPage = freeBoardService.getPosts(1, "");

        assertThat(firstPage.getContent()).hasSize(10);
        assertThat(firstPage.getTotalElements()).isEqualTo(12);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.getContent().getFirst().title()).isEqualTo("게시글 12");
        assertThat(secondPage.getContent()).hasSize(2);
        assertThat(secondPage.getContent().getFirst().title()).isEqualTo("게시글 2");
    }

    @Test
    @WithMockUser(username = "freeauthor", roles = "MEMBER")
    void 제목이나_내용으로_게시글을_검색하고_검색어를_화면에_유지한다() throws Exception {
        savePost("풋살 참가자 모집", "이번 주 토요일 경기입니다.");
        savePost("유니폼 안내", "구매 신청을 받습니다.");
        savePost("일반 이야기", "검색 대상이 아닙니다.");

        Page<FreeBoardListItem> titleResult = freeBoardService.getPosts(0, "  풋살  ");
        Page<FreeBoardListItem> contentResult = freeBoardService.getPosts(0, "구매");

        assertThat(titleResult.getContent())
                .extracting(FreeBoardListItem::title)
                .containsExactly("풋살 참가자 모집");
        assertThat(contentResult.getContent())
                .extracting(FreeBoardListItem::title)
                .containsExactly("유니폼 안내");

        mockMvc.perform(get("/freeboards").param("keyword", "  풋살  "))
                .andExpect(status().isOk())
                .andExpect(view().name("freeboard/list"))
                .andExpect(model().attribute("keyword", "풋살"))
                .andExpect(model().attributeExists("postPage", "posts"));
    }

    @Test
    @WithMockUser(username = "freeauthor", roles = "MEMBER")
    void 회원은_게시글에_댓글을_작성하고_조회할_수_있다() throws Exception {
        FreeBoardPost post = savePost("댓글 게시글", "댓글을 작성합니다.");

        mockMvc.perform(post("/freeboards/{id}/comments", post.getId())
                        .with(csrf())
                        .param("content", "  첫 댓글입니다.  "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/freeboards/" + post.getId() + "?commentCreated"
                ));

        assertThat(commentRepository.count()).isEqualTo(1);
        assertThat(commentRepository.findAll().getFirst().getContent()).isEqualTo("첫 댓글입니다.");
        assertThat(freeBoardService.getPosts(0, "").getContent().getFirst().commentCount())
                .isEqualTo(1);

        mockMvc.perform(get("/freeboards/{id}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("freeboard/detail"))
                .andExpect(model().attribute("commentCount", 1))
                .andExpect(model().attributeExists("comments", "freeBoardCommentRequest"));
    }

    @Test
    @WithMockUser(username = "freeauthor", roles = "MEMBER")
    void 빈_내용으로는_댓글을_작성할_수_없다() throws Exception {
        FreeBoardPost post = savePost("댓글 검증", "빈 댓글은 안 됩니다.");

        mockMvc.perform(post("/freeboards/{id}/comments", post.getId())
                        .with(csrf())
                        .param("content", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("freeboard/detail"))
                .andExpect(model().attributeHasFieldErrors(
                        "freeBoardCommentRequest", "content"
                ));

        assertThat(commentRepository.count()).isZero();
    }

    @Test
    @WithMockUser(username = "freeauthor", roles = "MEMBER")
    void 댓글_작성자는_자신의_댓글을_수정하고_삭제할_수_있다() throws Exception {
        FreeBoardPost post = savePost("댓글 관리", "작성자 권한 테스트");
        FreeBoardComment comment = saveComment(post, author, "수정 전 댓글");

        mockMvc.perform(post("/freeboards/{postId}/comments/{commentId}/edit",
                        post.getId(), comment.getId())
                        .with(csrf())
                        .param("content", "  수정된 댓글  "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/freeboards/" + post.getId() + "?commentUpdated"
                ));

        FreeBoardComment updated = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(updated.getContent()).isEqualTo("수정된 댓글");
        assertThat(updated.getUpdatedAt()).isNotNull();

        mockMvc.perform(post("/freeboards/{postId}/comments/{commentId}/delete",
                        post.getId(), comment.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/freeboards/" + post.getId() + "?commentDeleted"
                ));

        assertThat(commentRepository.existsById(comment.getId())).isFalse();
    }

    @Test
    @WithMockUser(username = "othermember", roles = "MEMBER")
    void 다른_회원의_댓글은_수정하거나_삭제할_수_없다() throws Exception {
        FreeBoardPost post = savePost("타인 댓글", "권한 테스트");
        FreeBoardComment comment = saveComment(post, author, "작성자 댓글");

        mockMvc.perform(post("/freeboards/{postId}/comments/{commentId}/edit",
                        post.getId(), comment.getId())
                        .with(csrf())
                        .param("content", "권한 없는 수정"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));

        mockMvc.perform(post("/freeboards/{postId}/comments/{commentId}/delete",
                        post.getId(), comment.getId()).with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));

        assertThat(commentRepository.findById(comment.getId()).orElseThrow().getContent())
                .isEqualTo("작성자 댓글");
    }

    @Test
    @WithMockUser(username = "freeadmin", roles = "ADMIN")
    void 관리자는_다른_회원의_댓글을_삭제할_수_있다() throws Exception {
        FreeBoardPost post = savePost("관리자 댓글", "관리자 권한 테스트");
        FreeBoardComment comment = saveComment(post, author, "삭제 대상 댓글");

        mockMvc.perform(post("/freeboards/{postId}/comments/{commentId}/delete",
                        post.getId(), comment.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection());

        assertThat(commentRepository.existsById(comment.getId())).isFalse();
    }

    @Test
    @WithMockUser(username = "freeauthor", roles = "MEMBER")
    void 다른_게시글의_댓글_ID로는_수정할_수_없다() throws Exception {
        FreeBoardPost firstPost = savePost("첫 게시글", "첫 내용");
        FreeBoardPost secondPost = savePost("둘째 게시글", "둘째 내용");
        FreeBoardComment comment = saveComment(firstPost, author, "첫 게시글 댓글");

        mockMvc.perform(post("/freeboards/{postId}/comments/{commentId}/edit",
                        secondPost.getId(), comment.getId())
                        .with(csrf())
                        .param("content", "잘못된 수정"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));

        assertThat(commentRepository.findById(comment.getId()).orElseThrow().getContent())
                .isEqualTo("첫 게시글 댓글");
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

    private FreeBoardComment saveComment(FreeBoardPost post, Member commentAuthor, String content) {
        return commentRepository.save(new FreeBoardComment(post, commentAuthor, content));
    }
}
