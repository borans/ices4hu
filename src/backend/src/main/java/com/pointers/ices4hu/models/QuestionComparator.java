package com.pointers.ices4hu.models;

import java.util.Comparator;

public class QuestionComparator implements Comparator<Question> {
    @Override
    public int compare(Question o1, Question o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return 1;
        if (o2 == null) return -1;
        if (o1.getOrderId() == null && o2.getOrderId() == null) {
            if (o1.getId() < o2.getId()) return -1;
            else if (o1.getId() > o2.getId()) return 1;
            else return 0;
        }
        if (o1.getOrderId() == null) return 1;
        if (o2.getOrderId() == null) return -1;

        if (o1.getOrderId() < o2.getOrderId()) return -1;
        else if (o1.getOrderId() > o2.getOrderId()) return 1;
        else return 0;
    }

    public static QuestionComparator getInstance() {
        return new QuestionComparator();
    }
}
