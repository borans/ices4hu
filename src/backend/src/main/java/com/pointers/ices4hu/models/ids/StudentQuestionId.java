package com.pointers.ices4hu.models.ids;

import com.pointers.ices4hu.models.Question;
import com.pointers.ices4hu.models.User;

import java.io.Serializable;

public class StudentQuestionId implements Serializable {

    private User user;

    private Question question;

    public StudentQuestionId() {

    }

    public StudentQuestionId(User user, Question question) {
        this.user = user;
        this.question = question;
    }
}
