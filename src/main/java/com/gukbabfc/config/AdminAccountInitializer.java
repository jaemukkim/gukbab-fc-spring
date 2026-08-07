package com.gukbabfc.config;

import com.gukbabfc.member.dao.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class AdminAccountInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final String adminUsername;

    public AdminAccountInitializer(MemberRepository memberRepository,
                                   @Value("${app.admin.username:}") String adminUsername) {
        this.memberRepository = memberRepository;
        this.adminUsername = adminUsername;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        memberRepository.assignDefaultRoleToMembersWithoutRole();

        if (adminUsername.isBlank()) {
            return;
        }

        memberRepository.findByUsername(adminUsername)
                .ifPresentOrElse(
                        member -> {
                            member.promoteToAdmin();
                            log.info("관리자 계정이 설정되었습니다: {}", adminUsername);
                        },
                        () -> log.warn("관리자로 지정할 회원을 찾을 수 없습니다: {}", adminUsername)
                );
    }
}
