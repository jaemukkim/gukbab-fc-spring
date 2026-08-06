package com.gukbabfc.member;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void signup(SignupRequest request) {
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new SignupException("passwordConfirm", "비밀번호가 일치하지 않습니다.");
        }
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new SignupException("username", "이미 사용 중인 아이디입니다.");
        }

        Member member = new Member(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getName()
        );
        memberRepository.save(member);
    }
}
