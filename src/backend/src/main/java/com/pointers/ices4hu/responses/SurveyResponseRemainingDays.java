package com.pointers.ices4hu.responses;

import com.pointers.ices4hu.models.Survey;
import lombok.Data;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.DAYS;

@Data
public class SurveyResponseRemainingDays extends SurveyResponse {

    private Integer daysUntilDeadline;

    @Override
    public void buildFrom(Survey survey) {
        super.buildFrom(survey);

        if (survey.getDeadline() != null && survey.getDeadline().isAfter(LocalDateTime.now())) {
            daysUntilDeadline = (int)DAYS.between(LocalDateTime.now(), survey.getDeadline());
        } else {
            daysUntilDeadline = 0;
        }


    }

}
