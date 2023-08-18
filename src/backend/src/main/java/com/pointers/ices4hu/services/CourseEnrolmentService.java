package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.CourseEnrolmentRequest;
import com.pointers.ices4hu.models.User;
import com.pointers.ices4hu.repositories.CourseEnrolmentRequestRepository;
import com.pointers.ices4hu.repositories.UserRepository;
import com.pointers.ices4hu.responses.CourseEnrolmentRequestResponse;
import com.pointers.ices4hu.responses.CourseResponse;
import com.pointers.ices4hu.responses.MessageResponse;
import com.pointers.ices4hu.types.RequestStatusType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseEnrolmentService {

    private final CourseEnrolmentRequestRepository courseEnrolmentRequestRepository;
    private final UserRepository userRepository;

    public CourseEnrolmentService(CourseEnrolmentRequestRepository courseEnrolmentRequestRepository,
                                  UserRepository userRepository) {
        this.courseEnrolmentRequestRepository = courseEnrolmentRequestRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<Object> getPendingCourseEnrolmentRequests() {
        List<CourseEnrolmentRequest> courseEnrolmentRequests = courseEnrolmentRequestRepository
                .getCourseEnrolmentRequestsByStatusType(RequestStatusType.PENDING.getValue());

        List<CourseEnrolmentRequestResponse> courseEnrolmentRequestResponses = new ArrayList<>();

        for (CourseEnrolmentRequest courseEnrolmentRequest: courseEnrolmentRequests) {
            CourseEnrolmentRequestResponse courseEnrolmentRequestResponse = new CourseEnrolmentRequestResponse();

            CourseResponse courseResponse = new CourseResponse();
            courseResponse.buildFrom(courseEnrolmentRequest.getCourse());
            courseEnrolmentRequestResponse.setCourse(courseResponse);

            User student = courseEnrolmentRequest.getUser();

            courseEnrolmentRequestResponse.setRequestId(courseEnrolmentRequest.getRequestId());
            courseEnrolmentRequestResponse.setStudentName(student.getName());
            courseEnrolmentRequestResponse.setStudentSurname(student.getSurname());
            courseEnrolmentRequestResponse.setStudentEMail(student.getEmail());
            courseEnrolmentRequestResponse.setRequestTime(courseEnrolmentRequest.getRequestDateTime());

            courseEnrolmentRequestResponses.add(courseEnrolmentRequestResponse);
        }

        return new ResponseEntity<>(courseEnrolmentRequestResponses, HttpStatus.OK);

    }

    public ResponseEntity<Object> approveCourseEnrolmentRequest(Long requestId) {
        return approveDenyCourseEnrolmentRequest(requestId, true);
    }

    public ResponseEntity<Object> denyCourseEnrolmentRequest(Long requestId) {
        return approveDenyCourseEnrolmentRequest(requestId, false);
    }

    private ResponseEntity<Object> approveDenyCourseEnrolmentRequest(Long requestId, boolean approve) {



        CourseEnrolmentRequest courseEnrolmentRequest = courseEnrolmentRequestRepository
                .findById(requestId).orElse(null);

        if (courseEnrolmentRequest == null) {
            return new ResponseEntity<>(new MessageResponse("No such course enrolment request!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (courseEnrolmentRequest.getCourse() == null) {
            return new ResponseEntity<>(new MessageResponse("No such course!"),
                    HttpStatus.BAD_REQUEST);
        }

        RequestStatusType statusType = RequestStatusType
                .values()[courseEnrolmentRequest.getStatus().intValue()];

        if (statusType != RequestStatusType.PENDING) {
            return new ResponseEntity<>(new MessageResponse("The request is not in pending state!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (approve) {
            User student = courseEnrolmentRequest.getUser();

            if (student == null)
                return new ResponseEntity<>(new MessageResponse("No such student!"), HttpStatus.BAD_REQUEST);

            if (student.getCourses().contains(courseEnrolmentRequest.getCourse()))
                return new ResponseEntity<>(new MessageResponse("Student is already enrolled to the course!"),
                        HttpStatus.BAD_REQUEST);

            courseEnrolmentRequest.setStatus(RequestStatusType.APPROVED.getValue());
            student.getCourses().add(courseEnrolmentRequest.getCourse());
            userRepository.save(student);

        } else {
            courseEnrolmentRequest.setStatus(RequestStatusType.DENIED.getValue());
        }

        courseEnrolmentRequestRepository.save(courseEnrolmentRequest);

        return new ResponseEntity<>(new MessageResponse("Operation successful!"), HttpStatus.OK);
    }

}
