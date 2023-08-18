package com.pointers.ices4hu.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name="survey")
public class Survey {

    @Id
    @SequenceGenerator(
            name = "survey_sequence",
            sequenceName = "survey_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "survey_sequence"
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="instructor_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonIgnore
    private User userInstructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="creator_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonIgnore
    private User userCreator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="course_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonIgnore
    private Course course;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<Question> questionSet;


    private LocalDateTime creationDatetime;
    private LocalDateTime startingDatetime;
    private LocalDateTime deadline;
    private Integer trialCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUserInstructor() {
        return userInstructor;
    }

    public void setUserInstructor(User userInstructor) {
        this.userInstructor = userInstructor;
    }

    public User getUserCreator() {
        return userCreator;
    }

    public void setUserCreator(User userCreator) {
        this.userCreator = userCreator;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Set<Question> getQuestionSet() {
        return questionSet;
    }

    public void setQuestionSet(Set<Question> questionSet) {
        this.questionSet = questionSet;
    }

    public LocalDateTime getCreationDatetime() {
        return creationDatetime;
    }

    public void setCreationDatetime(LocalDateTime creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public LocalDateTime getStartingDatetime() {
        return startingDatetime;
    }

    public void setStartingDatetime(LocalDateTime startingDatetime) {
        this.startingDatetime = startingDatetime;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Integer getTrialCount() {
        return trialCount;
    }

    public void setTrialCount(Integer trialCount) {
        this.trialCount = trialCount;
    }
}
