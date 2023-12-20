package com.exam.examBbs.Domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Board {
    @Id
    private String boardId;
    private String title;
    private String content;
    private String author;
    private Date regDate;
    private Date updateDate;
}
