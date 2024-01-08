package com.exam.examBbs.repository;

import com.exam.examBbs.domain.MemberLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberLoginRepository extends JpaRepository<MemberLogin, Long>, JpaSpecificationExecutor<MemberLogin> {

    default Optional<MemberLogin> findActiveByEmail(String email) {
        return findOne((root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("email"), email),
                        criteriaBuilder.isNull(root.get("deactivatedDate"))
                ));
    }

    default Optional<MemberLogin> findActiveById(Long Id) {
        return findOne((root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("Id"), Id),
                        criteriaBuilder.isNull(root.get("deactivatedDate"))
                ));
    }

    @Query("SELECT ml.memberInfo.name FROM MemberLogin ml WHERE ml.memberId = :memberId AND ml.deactivatedDate IS NULL")
    Optional<String> findMemberNameByActiveMemberId(@Param("memberId") Long memberId);

}