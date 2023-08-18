package com.pointers.ices4hu.controllers;

import com.pointers.ices4hu.requests.CourseRegistrationEnrolmentRequest;
import com.pointers.ices4hu.responses.MessageResponse;
import com.pointers.ices4hu.services.CourseRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course_registration")
public class CourseRegistrationController {

    private final CourseRegistrationService courseRegistrationService;

    public CourseRegistrationController(CourseRegistrationService courseRegistrationService) {
        this.courseRegistrationService = courseRegistrationService;
    }

    @GetMapping("/student")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<Object> getCourses(@RequestParam String user) {
        return courseRegistrationService.getCourses(user);
    }

    @PostMapping("/student/enrolment_request")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<MessageResponse> sendCourseEnrolmentRequest(
            @RequestParam String user,
            @RequestBody CourseRegistrationEnrolmentRequest courseRegistrationEnrolmentRequest) {
        return courseRegistrationService.sendCourseEnrolmentRequest(user, courseRegistrationEnrolmentRequest);
    }

}
