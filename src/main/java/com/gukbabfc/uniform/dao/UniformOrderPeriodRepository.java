package com.gukbabfc.uniform.dao;

import com.gukbabfc.uniform.entity.UniformOrderPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 유니폼 신청 기간의 저장과 최신순 조회를 담당합니다.
 */
public interface UniformOrderPeriodRepository extends JpaRepository<UniformOrderPeriod, Long> {

    Optional<UniformOrderPeriod> findFirstByClosedFalseAndStartsAtLessThanEqualAndEndsAtGreaterThanOrderByEndsAtAsc(
            LocalDateTime startsAt,
            LocalDateTime endsAt
    );

    List<UniformOrderPeriod> findAllByOrderByCreatedAtDesc();
}
