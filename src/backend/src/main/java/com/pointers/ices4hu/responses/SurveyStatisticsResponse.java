package com.pointers.ices4hu.responses;

import lombok.Data;

import java.util.List;

@Data
public class SurveyStatisticsResponse {
    private String question;
    private List<String> answers;
    private String base64;
    private Boolean isMultipleChoice;
}
