package com.exam.examBbs.repository;

import com.exam.examBbs.domain.MemberInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberInfoRepository extends JpaRepository<MemberInfo, Long>, JpaSpecificationExecutor<MemberInfo> {
    @Query("SELECT mi FROM MemberInfo mi WHERE mi.memberInfoId = :memberInfoId AND mi.memberLogin.deactivatedDate IS NULL")
    Optional<MemberInfo> findActiveById(@Param("memberInfoId") Long memberInfoId);
}