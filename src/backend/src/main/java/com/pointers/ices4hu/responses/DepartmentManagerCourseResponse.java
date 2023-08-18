package com.pointers.ices4hu.responses;

import lombok.Data;

import java.util.List;

@Data
public class DepartmentManagerCourseResponse {
    private List<CourseResponseWithInstructorId> courses;
    private List<InstructorResponse> instructors;
}
