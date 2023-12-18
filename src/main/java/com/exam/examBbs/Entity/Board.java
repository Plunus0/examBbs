package com.exam.examBbs.Entity;

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

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getBoardId() {
        return boardId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public Date getRegDate() {
        return regDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

}
