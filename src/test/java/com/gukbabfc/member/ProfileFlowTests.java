package com.gukbabfc.member;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * 프로필 조회와 수정 흐름을 검증하는 통합 테스트입니다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProfileFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        if (!memberRepository.existsByUsername("profileuser")) {
            memberRepository.save(new Member("profileuser", passwordEncoder.encode("password1234"), "프로필선수"));
        }
    }

    @Test
    void 로그인하지_않으면_마이페이지에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/mypage"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "profileuser", roles = "MEMBER")
    void 내_프로필을_조회할_수_있다() throws Exception {
        mockMvc.perform(get("/mypage"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/mypage"))
                .andExpect(model().attributeExists("profile"));
    }

    @Test
    @WithMockUser(username = "profileuser", roles = "MEMBER")
    void 프로필을_수정할_수_있다() throws Exception {
        mockMvc.perform(post("/mypage/edit")
                        .with(csrf())
                        .param("name", "든든한미드필더")
                        .param("position", "MF")
                        .param("backNumber", "8")
                        .param("introduction", "국밥처럼 든든하게 뛰겠습니다."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage?updated"));

        Member member = memberRepository.findByUsername("profileuser").orElseThrow();
        assertThat(member.getName()).isEqualTo("든든한미드필더");
        assertThat(member.getPosition()).isEqualTo("MF");
        assertThat(member.getBackNumber()).isEqualTo(8);
        assertThat(member.getIntroduction()).isEqualTo("국밥처럼 든든하게 뛰겠습니다.");
    }
}
