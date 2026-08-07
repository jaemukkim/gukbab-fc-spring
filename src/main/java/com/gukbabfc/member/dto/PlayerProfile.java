package com.gukbabfc.member.dto;

import com.gukbabfc.member.Member;

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
