package com.pointers.ices4hu.models;

import java.util.Comparator;

public class CourseComparator implements Comparator<Course> {
    @Override
    public int compare(Course o1, Course o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return 1;
        if (o2 == null) return -1;
        if (o1.getId() == null && o2.getId() == null) return 0;
        if (o1.getId() == null) return 1;
        if (o2.getId() == null) return -1;

        if (o1.getId() < o2.getId()) return -1;
        else if (o1.getId() > o2.getId()) return 1;
        else return 0;
    }

    public static CourseComparator getInstance() {
        return new CourseComparator();
    }
}
