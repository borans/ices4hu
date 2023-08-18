package com.pointers.ices4hu.controllers;

import com.pointers.ices4hu.requests.AddCourseRequest;
import com.pointers.ices4hu.requests.AssignInstructorRequest;
import com.pointers.ices4hu.responses.MessageResponse;
import com.pointers.ices4hu.services.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


/**
 * Course Controller Class
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private CourseService courseService;

    /**
     * Course Controller Constructor
     * @param courseService Course Service
     */
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/student")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<Object> getCourses(@RequestParam String user) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(user))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(courseService.getCourseResponses(user), HttpStatus.OK);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> getCoursesForAdmin() {
        return new ResponseEntity<>(courseService.getCourseResponsesOfAdmin(), HttpStatus.OK);
    }


    @GetMapping("/department_manager")
    @PreAuthorize("hasAuthority('department_manager')")
    public ResponseEntity<Object> getCoursesForDepartmentManager(@RequestParam String user) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(user))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(courseService.getCourseResponsesOfDepartmentManager(user),
                HttpStatus.OK);
    }



    @GetMapping("/instructor")
    @PreAuthorize("hasAuthority('instructor')")
    public ResponseEntity<Object> getCoursesForInstructor(@RequestParam String user) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(user))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(courseService.getCourseResponsesOfInstructor(user), HttpStatus.OK);
    }

    @PutMapping("/department_manager/assign_instructors")
    @PreAuthorize("hasAuthority('department_manager')")
    public ResponseEntity<Object> assignInstructors(@RequestParam String user,
                                                    @RequestBody AssignInstructorRequest assignInstructorRequest) {
        return courseService.assignInstructors(user, assignInstructorRequest);
    }

    @PostMapping("/admin/create_course")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> createCourse(@RequestBody AddCourseRequest addCourseRequest) {
        return courseService.createCourse(addCourseRequest);
    }

    @PutMapping("/admin")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> editCourse(@RequestParam Long course,
                                             @RequestBody AddCourseRequest addCourseRequest) {
        return courseService.editCourse(course, addCourseRequest);
    }

    @DeleteMapping("/admin")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> deleteCourse(@RequestParam Long course) {
        return courseService.deleteCourse(course);
    }

}
