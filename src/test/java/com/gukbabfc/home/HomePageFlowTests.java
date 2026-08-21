package com.gukbabfc.home;

import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * 로그인 상태에 따라 메인 소개 화면과 대시보드가 정상 렌더링되는지 확인합니다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class HomePageFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void 로그인전에는기능소개화면을표시한다() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("signedIn", false))
                .andExpect(model().attributeDoesNotExist("dashboard"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("주요 기능 소개")));
    }

    @Test
    void 로그인후에는대시보드를표시한다() throws Exception {
        String username = "home-" + UUID.randomUUID();
        memberRepository.save(new Member(username, passwordEncoder.encode("password"), "대시보드 회원"));

        mockMvc.perform(get("/").with(user(username)))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("signedIn", true))
                .andExpect(model().attributeExists("dashboard"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("팀 대시보드")));
    }
}
