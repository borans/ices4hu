package com.pointers.ices4hu.models;

import java.util.Comparator;

public class SurveyComparator implements Comparator<Survey> {

    public int compare(Survey s1, Survey s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return 1;
        if (s2 == null) return -1;
        if (s1.getDeadline() == null && s2.getDeadline() == null) return 0;
        if (s1.getDeadline() == null) return 1;
        if (s2.getDeadline() == null) return -1;

        if (s1.getDeadline().isBefore(s2.getDeadline())) return -1;
        else if (s1.getDeadline().isAfter(s2.getDeadline())) return 1;
        else return 0;
    }

    public static SurveyComparator getInstance() {
        return new SurveyComparator();
    }

}
