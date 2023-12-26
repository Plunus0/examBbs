package com.exam.examBbs.service;

import com.exam.examBbs.domain.Board;
import org.springframework.data.jpa.domain.Specification;


public class BoardSpecifications {

    public static Specification<Board> titleContains(String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.trim().toLowerCase() + "%");
    }

    public static Specification<Board> contentContains(String content) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), "%" + content.trim().toLowerCase() + "%");
    }

    public static Specification<Board> authorNameContains(String authorName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("author").get("name")), "%" + authorName.trim().toLowerCase() + "%");
    }

    public static Specification<Board> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("deactivatedDate"));
    }
}
