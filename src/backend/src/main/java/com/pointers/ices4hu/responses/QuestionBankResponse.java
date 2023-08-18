package com.pointers.ices4hu.responses;

import com.pointers.ices4hu.requests.RequestQuestion;
import lombok.Data;

import java.util.List;

@Data
public class QuestionBankResponse {

    private Long id;
    private List<RequestQuestion> questions;

}