package com.example.demo.Question;

import com.example.demo.Answer.Answer;
import com.example.demo.User.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    // 글쓴이
    @ManyToOne
    private SiteUser author;

    // 좋아요
    @ManyToMany
    Set<SiteUser> voter;

    // 조회수
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;
}
