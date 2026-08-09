package com.gukbabfc.member.service;

import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.dto.PlayerProfile;
import com.gukbabfc.member.dto.ProfileUpdateRequest;
import com.gukbabfc.member.dto.SignupRequest;
import com.gukbabfc.member.entity.Member;
import com.gukbabfc.member.exception.SignupException;
import com.gukbabfc.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 회원가입, 회원 조회, 프로필 변경 업무 규칙을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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
                .orElseThrow(MemberNotFoundException::new);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
