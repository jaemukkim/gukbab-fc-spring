package com.gukbabfc.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
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

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Member() {
    }

    public Member(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getPosition() {
        return position;
    }

    public Integer getBackNumber() {
        return backNumber;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void updateProfile(String name, String position, Integer backNumber, String introduction) {
        this.name = name;
        this.position = position;
        this.backNumber = backNumber;
        this.introduction = introduction;
    }
}
