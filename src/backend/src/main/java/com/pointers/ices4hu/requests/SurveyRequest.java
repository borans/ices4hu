package com.pointers.ices4hu.requests;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SurveyRequest {

    // private Long instructorId;
    private Long courseId;
    private LocalDateTime creationDatetime;
    private LocalDateTime startingDatetime;
    private LocalDateTime deadline;
    private List<RequestQuestion> questions;
}
