package com.gukbabfc.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원 계정과 선수 프로필 정보를 저장하는 JPA 엔티티입니다.
 */
@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(length = 10)
    private String position;

    private Integer backNumber;

    @Column(length = 300)
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MemberRole role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Member(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = MemberRole.MEMBER;
        this.createdAt = LocalDateTime.now();
    }

    public void updateProfile(String name, String position, Integer backNumber, String introduction) {
        this.name = name;
        this.position = position;
        this.backNumber = backNumber;
        this.introduction = introduction;
    }

    public MemberRole getRole() {
        return role == null ? MemberRole.MEMBER : role;
    }

    public void promoteToAdmin() {
        this.role = MemberRole.ADMIN;
    }
}
