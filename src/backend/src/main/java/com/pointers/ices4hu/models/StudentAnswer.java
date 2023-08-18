package com.pointers.ices4hu.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pointers.ices4hu.models.ids.StudentQuestionId;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@IdClass(StudentQuestionId.class)
@Table(name="student_answer")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StudentAnswer {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="student_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION) //TODO NOACTION MI CASCADE MI
    @JsonIgnore
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="question_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION) //TODO NOACTION MI CASCADE MI
    @JsonIgnore
    private Question question;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="multiple_choice_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonIgnore
    private MultipleChoice multipleChoice;

    @Column(columnDefinition="text")
    private String content;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public MultipleChoice getMultipleChoice() {
        return multipleChoice;
    }

    public void setMultipleChoice(MultipleChoice multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
