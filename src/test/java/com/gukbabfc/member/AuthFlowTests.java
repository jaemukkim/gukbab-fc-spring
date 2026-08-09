package com.gukbabfc.member;

import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.entity.Member;
import com.gukbabfc.member.entity.MemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * 회원가입과 로그인 보안 흐름을 검증하는 통합 테스트입니다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void 회원가입한_계정으로_로그인할_수_있다() throws Exception {
        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("username", "gukbab")
                        .param("name", "국밥이")
                        .param("password", "gukbab1234")
                        .param("passwordConfirm", "gukbab1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?signup"));

        Member member = memberRepository.findByUsername("gukbab").orElseThrow();
        assertThat(member.getPassword()).isNotEqualTo("gukbab1234");
        assertThat(passwordEncoder.matches("gukbab1234", member.getPassword())).isTrue();
        assertThat(member.getRole()).isEqualTo(MemberRole.MEMBER);

        mockMvc.perform(formLogin().user("gukbab").password("gukbab1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void 회원가입_화면을_누구나_볼_수_있다() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/signup"));
    }
}
