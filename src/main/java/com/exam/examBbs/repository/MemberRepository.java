package com.exam.examBbs.repository;

import com.exam.examBbs.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {

    default Optional<Member> findActiveByEmail(String email) {
        return findOne((root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("email"), email),
                        criteriaBuilder.isNull(root.get("deactivatedDate"))
                ));
    }

    default Optional<Member> findActiveById(Long Id) {
        return findOne((root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("Id"), Id),
                        criteriaBuilder.isNull(root.get("deactivatedDate"))
                ));
    }
}