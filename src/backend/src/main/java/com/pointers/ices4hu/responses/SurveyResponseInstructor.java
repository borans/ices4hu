package com.pointers.ices4hu.responses;

import com.pointers.ices4hu.models.Survey;
import com.pointers.ices4hu.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SurveyResponseInstructor extends SurveyResponse {

    private Double completedPercent;

    // this method does not fill completedPercent since it is a detailed attribute
    // the caller must handle it

    @Override
    public void buildFrom(Survey survey) {
        super.buildFrom(survey);
    }
}
