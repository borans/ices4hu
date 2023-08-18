package com.pointers.ices4hu.responses;

import com.pointers.ices4hu.models.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuestionWithAnswer {
    private Question question;
    private String answer;
}
