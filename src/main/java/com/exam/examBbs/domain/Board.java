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
    private Long viewCount = 0L; // 조회수
    private LocalDateTime deactivatedDate; // 비활성화 날짜

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

}
