package com.pointers.ices4hu.requests;

import lombok.Data;

import java.util.List;

@Data
public class RequestQuestion {

    private Long id;
    private String question;
    private Boolean isMultipleChoice;
    private List<RequestMultipleChoice> multipleChoices;

}
