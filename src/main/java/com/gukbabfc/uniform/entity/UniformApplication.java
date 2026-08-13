package com.gukbabfc.uniform.entity;

import com.gukbabfc.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 신청 기간별 회원의 사이즈, 등번호, 마킹, 수량을 저장합니다.
 */
@Entity
@Table(
        name = "uniform_applications",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_uniform_application_period_member",
                columnNames = {"period_id", "member_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UniformApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "period_id", nullable = false)
    private UniformOrderPeriod period;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UniformSize size;

    @Column(nullable = false)
    private Integer backNumber;

    @Column(nullable = false, length = 20)
    private String markingName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public UniformApplication(UniformOrderPeriod period, Member member, UniformSize size,
                              Integer backNumber, String markingName, Integer quantity) {
        this.period = period;
        this.member = member;
        this.size = size;
        this.backNumber = backNumber;
        this.markingName = markingName;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
    }

    public void update(UniformSize size, Integer backNumber, String markingName, Integer quantity) {
        this.size = size;
        this.backNumber = backNumber;
        this.markingName = markingName;
        this.quantity = quantity;
        this.updatedAt = LocalDateTime.now();
    }
}
