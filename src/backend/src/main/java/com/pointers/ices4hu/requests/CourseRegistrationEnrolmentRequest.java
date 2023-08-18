package com.pointers.ices4hu.requests;

import lombok.Data;

import java.util.List;

@Data
public class CourseRegistrationEnrolmentRequest {
    private List<CourseRequest> courses;
}
