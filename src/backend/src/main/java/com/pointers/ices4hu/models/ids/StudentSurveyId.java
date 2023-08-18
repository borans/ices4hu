package com.pointers.ices4hu.models.ids;

import com.pointers.ices4hu.models.Survey;
import com.pointers.ices4hu.models.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public class StudentSurveyId implements Serializable {
    private User user;
    private Survey survey;

    public StudentSurveyId() {

    }

    public StudentSurveyId(User user, Survey survey) {
        this.user = user;
        this.survey = survey;
    }
}
