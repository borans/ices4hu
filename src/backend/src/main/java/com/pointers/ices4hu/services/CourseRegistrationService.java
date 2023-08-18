package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.Course;
import com.pointers.ices4hu.models.CourseEnrolmentRequest;
import com.pointers.ices4hu.models.User;
import com.pointers.ices4hu.repositories.CourseEnrolmentRequestRepository;
import com.pointers.ices4hu.repositories.CourseRepository;
import com.pointers.ices4hu.requests.CourseRegistrationEnrolmentRequest;
import com.pointers.ices4hu.requests.CourseRequest;
import com.pointers.ices4hu.responses.CourseResponse;
import com.pointers.ices4hu.responses.MessageResponse;
import com.pointers.ices4hu.types.RequestStatusType;
import com.pointers.ices4hu.types.StudentDegree;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CourseRegistrationService {

     private final CourseEnrolmentRequestRepository courseEnrolmentRequestRepository;
     private final CourseRepository courseRepository;
     private final UserService userService;

    public CourseRegistrationService(CourseEnrolmentRequestRepository courseEnrolmentRequestRepository,
                                     CourseRepository courseRepository,
                                     UserService userService) {
        this.courseEnrolmentRequestRepository = courseEnrolmentRequestRepository;
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    public ResponseEntity<Object> getCourses(String user) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(user))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User student = userService.getUserByLoginID(user);


        //List<Course> eligibleCourses = courseRepository.findCoursesForCourseRegistration(student.getId(),
        //        RequestStatusType.DENIED.getValue(), student.getDepartment().getId());

        List<Course> courses = courseRepository.findCoursesByDepartmentId(student.getDepartment().getId());

        Set<Course> courseSet = student.getCourses();

        Set<Long> courseIdSet = new HashSet<>();

        for (Course course: courseSet)
            courseIdSet.add(course.getId());

        Set<Long> addedCourses = new HashSet<>();

        List<CourseResponse> courseResponses = new ArrayList<>();

        for (Course course: courses) {
            if (!addedCourses.contains(course.getId()) && !courseIdSet.contains(course.getId())) {
                if ((course.getUndergraduate() && student.getDegree() != StudentDegree.UNDERGRADUATE.getValue())
                || (!course.getUndergraduate() && student.getDegree() == StudentDegree.UNDERGRADUATE.getValue())) {
                    continue;
                }

                List<CourseEnrolmentRequest> courseEnrolmentRequests = courseEnrolmentRequestRepository
                        .getCourseEnrolmentRequestsByCourseAndStudentIdsWithStatusType(course.getId(),
                        student.getId(), RequestStatusType.PENDING.getValue());

                if (courseEnrolmentRequests.size() > 0)
                    continue;

                CourseResponse courseResponse = new CourseResponse();
                courseResponse.buildFrom(course);
                courseResponses.add(courseResponse);
                addedCourses.add(course.getId());
            }
        }

        return new ResponseEntity<>(courseResponses, HttpStatus.OK);

    }

    public ResponseEntity<MessageResponse> sendCourseEnrolmentRequest(
            String user,
            CourseRegistrationEnrolmentRequest courseRegistrationEnrolmentRequest) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(user))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User student = userService.getUserByLoginID(user);

        List<CourseRequest> courseRequests = courseRegistrationEnrolmentRequest.getCourses();
        if (courseRequests.size() == 0) {
            return new ResponseEntity<>(new MessageResponse("No course is selected!"), HttpStatus.BAD_REQUEST);
        }
        for (CourseRequest courseRequest: courseRequests) {
            Optional<Course> courseOptional = courseRepository.findById(courseRequest.getId());
            if (!courseOptional.isPresent())
                continue;

            Course course = courseOptional.get();

            List<CourseEnrolmentRequest> courseEnrolmentRequests = courseEnrolmentRequestRepository
                    .getCourseEnrolmentRequestsByCourseAndStudentIds(course.getId(),
                            student.getId(),
                            RequestStatusType.DENIED.getValue());

            /* There is already a course enrolment request made by
             * the student for the course. And the request was either approved or is pending.
             * Therefore, go to next iteration */
            if (courseEnrolmentRequests.size() > 0)
                continue;

            /* The student already takes the course. Go to the next iteration */
            if (student.getCourses().contains(course))
                continue;

            CourseEnrolmentRequest courseEnrolmentRequest = new CourseEnrolmentRequest();
            courseEnrolmentRequest.setCourse(course);
            courseEnrolmentRequest.setUser(student);
            courseEnrolmentRequest.setStatus(RequestStatusType.PENDING.getValue());
            courseEnrolmentRequest.setRequestDateTime(LocalDateTime.now());
            courseEnrolmentRequestRepository.save(courseEnrolmentRequest);
        }

        return new ResponseEntity<>(new MessageResponse("Operation successful!"), HttpStatus.OK);
    }
}
