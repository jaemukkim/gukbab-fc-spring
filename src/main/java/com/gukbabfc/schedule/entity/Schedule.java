package com.gukbabfc.schedule.entity;

import com.gukbabfc.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 풋살 경기의 일시, 장소, 안내를 저장하는 JPA 엔티티입니다.
 */
@Entity
@Table(name = "schedules")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    @Lob
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Schedule(String title, String location, LocalDateTime scheduledAt,
                    String description, Member createdBy) {
        this.title = title;
        this.location = location;
        this.scheduledAt = scheduledAt;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String location, LocalDateTime scheduledAt, String description) {
        this.title = title;
        this.location = location;
        this.scheduledAt = scheduledAt;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
}
