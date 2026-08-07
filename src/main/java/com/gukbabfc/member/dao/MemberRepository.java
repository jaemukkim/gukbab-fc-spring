package com.gukbabfc.member.dao;

import com.gukbabfc.member.entity.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);

    List<Member> findAllByOrderByNameAsc();

    @Modifying
    @Query("update Member member set member.role = com.gukbabfc.member.entity.MemberRole.MEMBER where member.role is null")
    int assignDefaultRoleToMembersWithoutRole();
}
