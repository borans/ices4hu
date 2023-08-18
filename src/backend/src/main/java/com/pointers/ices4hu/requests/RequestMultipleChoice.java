package com.pointers.ices4hu.requests;

import lombok.Data;

@Data
public class RequestMultipleChoice implements Comparable<RequestMultipleChoice> {
    private Long id;
    private String content;

    public int compareTo(RequestMultipleChoice other) {
        if (this.id < other.id) return -1;
        else if (this.id > other.id) return 1;
        else return 0;
    }


}
