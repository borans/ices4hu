package com.pointers.ices4hu.requests;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class Answer {
    private Long questionId;
    private Long multipleChoiceId;
    private String content;

    public boolean isEmpty() {
        return !StringUtils.hasText(content) && multipleChoiceId == null;
    }

    public boolean isAnswered() {
        return !isEmpty();
    }
}