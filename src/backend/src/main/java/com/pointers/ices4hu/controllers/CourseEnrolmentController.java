package com.pointers.ices4hu.controllers;

import com.pointers.ices4hu.services.CourseEnrolmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course_enrolment")
public class CourseEnrolmentController {

    private final CourseEnrolmentService courseEnrolmentService;

    public CourseEnrolmentController(CourseEnrolmentService courseEnrolmentService) {
        this.courseEnrolmentService = courseEnrolmentService;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> getPendingCourseEnrolmentRequests() {
        return courseEnrolmentService.getPendingCourseEnrolmentRequests();
    }

    @PostMapping("/admin/approve")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> approveCourseEnrolmentRequest(@RequestParam Long request) {
        return courseEnrolmentService.approveCourseEnrolmentRequest(request);
    }

    @PostMapping("/admin/deny")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> denyCourseEnrolmentRequest(@RequestParam Long request) {
        return courseEnrolmentService.denyCourseEnrolmentRequest(request);
    }

}
