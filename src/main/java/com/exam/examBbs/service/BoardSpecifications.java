package com.exam.examBbs.service;

import com.exam.examBbs.domain.Board;
import org.springframework.data.jpa.domain.Specification;


public class BoardSpecifications {

    public static Specification<Board> titleContains(String title) {
        return (root, query, criteriaBuilder) ->
                title == null ? criteriaBuilder.isTrue(criteriaBuilder.literal(true)) :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.trim().toLowerCase() + "%");

    }

    public static Specification<Board> contentContains(String content) {
        return (root, query, criteriaBuilder) ->
                content == null ? criteriaBuilder.isTrue(criteriaBuilder.literal(true)) :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), "%" + content.trim().toLowerCase() + "%");
    }

    public static Specification<Board> authorNameContains(String authorName) {
        System.out.println("author middle, search_text : "+authorName);
        return (root, query, criteriaBuilder) -> {
            if (authorName == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("author").get("name")), "%" + authorName.trim().toLowerCase() + "%");
        };
    }

}
