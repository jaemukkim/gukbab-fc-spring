package com.gukbabfc.uniform.dao;

import com.gukbabfc.uniform.entity.UniformApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 회원별 유니폼 신청의 저장, 조회, 취소를 담당합니다.
 */
public interface UniformApplicationRepository extends JpaRepository<UniformApplication, Long> {

    Optional<UniformApplication> findByPeriodIdAndMemberId(Long periodId, Long memberId);

    @Query("""
            select application
            from UniformApplication application
            join fetch application.member
            where application.period.id = :periodId
            order by application.createdAt asc, application.id asc
            """)
    List<UniformApplication> findAllWithMemberByPeriodId(@Param("periodId") Long periodId);
}
