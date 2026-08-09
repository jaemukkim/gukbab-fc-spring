package com.gukbabfc.member.dto;

import com.gukbabfc.member.entity.Member;

/**
 * 화면에 노출할 선수 프로필 정보를 전달합니다.
 */
public record PlayerProfile(
        Long id,
        String username,
        String name,
        String position,
        Integer backNumber,
        String introduction
) {
    public static PlayerProfile from(Member member) {
        return new PlayerProfile(
                member.getId(),
                member.getUsername(),
                member.getName(),
                member.getPosition(),
                member.getBackNumber(),
                member.getIntroduction()
        );
    }
}
