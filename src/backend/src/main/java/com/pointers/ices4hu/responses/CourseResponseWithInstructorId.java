package com.pointers.ices4hu.responses;

import com.pointers.ices4hu.models.Course;
import lombok.Data;

@Data
public class CourseResponseWithInstructorId extends CourseResponse {
    private Long instructorId;

    @Override
    public void buildFrom(Course course) {
        super.buildFrom(course);
        if (course.getUser() != null)
            setInstructorId(course.getUser().getId());
    }

}
