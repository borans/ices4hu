package com.pointers.ices4hu.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pointers.ices4hu.models.ids.StudentSurveyId;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@IdClass(StudentSurveyId.class)
@Table(name="student_survey_fill")
public class StudentSurveyFill {

    @Id
    @ManyToOne
    @JoinColumn(name="student_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonIgnore
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name="survey_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Survey survey;

    private int trialCount;

    private LocalDateTime completionDatetime;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public int getTrialCount() {
        return trialCount;
    }

    public void setTrialCount(int trialCount) {
        this.trialCount = trialCount;
    }

    public LocalDateTime getCompletionDatetime() {
        return completionDatetime;
    }

    public void setCompletionDatetime(LocalDateTime completionDatetime) {
        this.completionDatetime = completionDatetime;
    }
}
