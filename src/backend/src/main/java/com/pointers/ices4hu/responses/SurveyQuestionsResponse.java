package com.pointers.ices4hu.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyQuestionsResponse {

    private List<QuestionWithAnswer> questions;
    private Integer trialCount;

}
