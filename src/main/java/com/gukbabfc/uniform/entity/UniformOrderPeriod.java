package com.gukbabfc.uniform.entity;

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
 * 유니폼 구매 신청의 제목, 기간, 안내와 마감 상태를 저장합니다.
 */
@Entity
@Table(name = "uniform_order_periods")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UniformOrderPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private LocalDateTime endsAt;

    @Column(nullable = false)
    private boolean closed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public UniformOrderPeriod(String title, String description, LocalDateTime startsAt,
                              LocalDateTime endsAt, Member createdBy) {
        this.title = title;
        this.description = description;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String description, LocalDateTime startsAt, LocalDateTime endsAt) {
        this.title = title;
        this.description = description;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.updatedAt = LocalDateTime.now();
    }

    public void close() {
        this.closed = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void reopen() {
        this.closed = false;
        this.updatedAt = LocalDateTime.now();
    }
}
