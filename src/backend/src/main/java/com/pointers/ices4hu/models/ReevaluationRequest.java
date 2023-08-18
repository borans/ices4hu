package com.pointers.ices4hu.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name="reevaluation_request")
public class ReevaluationRequest {

    @Id
    @SequenceGenerator(
            name = "reevaluation_request_sequence",
            sequenceName = "reevaluation_request_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "reevaluation_request_sequence"
    )
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="request_by")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonIgnore
    private User userRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="evaluated_by")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonIgnore
    private User userEvaluate;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="survey_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Survey survey;

    private Byte status;
    private LocalDateTime requestDatetime;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public User getUserRequest() {
        return userRequest;
    }

    public void setUserRequest(User userRequest) {
        this.userRequest = userRequest;
    }

    public User getUserEvaluate() {
        return userEvaluate;
    }

    public void setUserEvaluate(User userEvaluate) {
        this.userEvaluate = userEvaluate;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public LocalDateTime getRequestDatetime() {
        return requestDatetime;
    }

    public void setRequestDatetime(LocalDateTime requestDatetime) {
        this.requestDatetime = requestDatetime;
    }
}
