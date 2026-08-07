package com.gukbabfc.member.service;

import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.dto.PlayerProfile;
import com.gukbabfc.member.dto.ProfileUpdateRequest;
import com.gukbabfc.member.dto.SignupRequest;
import com.gukbabfc.member.entity.Member;
import com.gukbabfc.member.exception.SignupException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(readOnly = true)
    public PlayerProfile getProfile(String username) {
        return PlayerProfile.from(findMember(username));
    }

    @Transactional(readOnly = true)
    public List<PlayerProfile> getPlayers() {
        return memberRepository.findAllByOrderByNameAsc().stream()
                .map(PlayerProfile::from)
                .toList();
    }

    @Transactional
    public void updateProfile(String username, ProfileUpdateRequest request) {
        Member member = findMember(username);
        member.updateProfile(
                request.getName().trim(),
                normalize(request.getPosition()),
                request.getBackNumber(),
                normalize(request.getIntroduction())
        );
    }

    private Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
