package com.exam.examBbs.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "board")
public class Board {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_seq_gen")
    @SequenceGenerator(name = "board_seq_gen", sequenceName = "board_seq", allocationSize = 1)
    @Column(name = "board_id")
    @Id
    private Long boardId;
    private String title;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private Member author;
    @Column(name = "reg_date")
    private LocalDateTime regDate;
    @Column(name = "update_date")
    private LocalDateTime updateDate;
    private String password;
    @Builder.Default
    private Long viewCount = 0L;
    private LocalDateTime deactivatedDate; // 간접 비활성화 날짜
    private LocalDateTime deprecatedDate; // 직접 비활성화 날짜
    //비활성화 해제 누가??

    public Board increaseViewCount() {
        return Board.builder()
                .boardId(this.boardId)
                .title(this.title)
                .content(this.content)
                .author(this.author)
                .regDate(this.regDate)
                .updateDate(this.updateDate)
                .viewCount(this.viewCount + 1)
                .build();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updateDate = LocalDateTime.now();
    }

    public void deactivate(LocalDateTime dateTime, boolean isByAdmin) {
        if (isByAdmin) {
            this.deactivatedDate = dateTime;
        } else {
            this.deprecatedDate = dateTime;
        }
    }

    public void active(){
        this.deactivatedDate = null;
    }

}
