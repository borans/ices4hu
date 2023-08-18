package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.Department;
import com.pointers.ices4hu.models.EnrolmentRequest;
import com.pointers.ices4hu.models.User;
import com.pointers.ices4hu.repositories.EnrolmentRequestRepository;
import com.pointers.ices4hu.responses.MessageResponse;
import com.pointers.ices4hu.responses.UserEnrolmentRequestResponse;
import com.pointers.ices4hu.types.RequestStatusType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserEnrolmentService {

    private final EnrolmentRequestRepository enrolmentRequestRepository;
    private final UserService userService;

    public UserEnrolmentService(EnrolmentRequestRepository enrolmentRequestRepository, UserService userService) {
        this.enrolmentRequestRepository = enrolmentRequestRepository;
        this.userService = userService;
    }

    public ResponseEntity<Object> getPendingUserEnrolmentRequests() {
        List<EnrolmentRequest> enrolmentRequests = enrolmentRequestRepository
                .getEnrolmentRequestsByStatusType(RequestStatusType.PENDING.getValue());

        List<UserEnrolmentRequestResponse> userEnrolmentRequestResponses = new ArrayList<>();

        for (EnrolmentRequest enrolmentRequest: enrolmentRequests) {
            UserEnrolmentRequestResponse userEnrolmentRequestResponse = new UserEnrolmentRequestResponse();
            userEnrolmentRequestResponse.buildFrom(enrolmentRequest);
            userEnrolmentRequestResponses.add(userEnrolmentRequestResponse);
        }

        return new ResponseEntity<>(userEnrolmentRequestResponses, HttpStatus.OK);
    }

    public ResponseEntity<Object> approveUserEnrolmentRequest(Long requestId) {
        return approveDenyUserEnrolmentRequest(requestId, true);
    }

    public ResponseEntity<Object> denyUserEnrolmentRequest(Long requestId) {
        return approveDenyUserEnrolmentRequest(requestId, false);
    }

    private ResponseEntity<Object> approveDenyUserEnrolmentRequest(Long requestId, boolean approve) {
        EnrolmentRequest enrolmentRequest = enrolmentRequestRepository.findById(requestId).orElse(null);
        if (enrolmentRequest == null) {
            return new ResponseEntity<>(new MessageResponse("No such user enrolment request!"),
                    HttpStatus.BAD_REQUEST);
        }

        Department department = enrolmentRequest.getDepartment();
        if (department == null) {
            return new ResponseEntity<>(new MessageResponse("No such department!"),
                    HttpStatus.BAD_REQUEST);
        }

        RequestStatusType statusType = RequestStatusType
                .values()[enrolmentRequest.getStatus().intValue()];

        if (statusType != RequestStatusType.PENDING) {
            return new ResponseEntity<>(new MessageResponse("The request is not in pending state!"),
                    HttpStatus.BAD_REQUEST);
        }


        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userService.getUserByLoginID(username);

        enrolmentRequest.setEvaluationDateTime(LocalDateTime.now());
        enrolmentRequest.setUser(admin);

        if (approve) {
            enrolmentRequest.setStatus(RequestStatusType.APPROVED.getValue());

            String email = enrolmentRequest.getEmail();
            String loginID = email.split("@")[0];

            if (!StringUtils.hasText(loginID)) {
                return new ResponseEntity<>(new MessageResponse("Bad email address!"),
                        HttpStatus.BAD_REQUEST);
            }

            if (!email.split("@")[1].equals("hacettepe.edu.tr")) {
                return new ResponseEntity<>(new MessageResponse("Only Hacettepe mail addresses are accepted!"),
                        HttpStatus.BAD_REQUEST);
            }

            if (userService.getUserByLoginID(loginID) != null) {
                return new ResponseEntity<>(new MessageResponse("Login Id already exists!"),
                        HttpStatus.BAD_REQUEST);
            }

            String name = enrolmentRequest.getName();
            if (!StringUtils.hasText(name)) {
                return new ResponseEntity<>(new MessageResponse("Name cannot be empty!"),
                        HttpStatus.BAD_REQUEST);
            }

            String surname = enrolmentRequest.getSurname();
            if (!StringUtils.hasText(surname)) {
                return new ResponseEntity<>(new MessageResponse("Surname cannot be empty!"),
                        HttpStatus.BAD_REQUEST);
            }

            Byte userType = enrolmentRequest.getUserType();
            if (userType == null) {
                return new ResponseEntity<>(new MessageResponse("User type cannot be empty!"),
                        HttpStatus.BAD_REQUEST);
            }

            Byte degree = enrolmentRequest.getDegree();

            userService.createUser(loginID, name, surname, email, userType, department, degree);

        } else {
            enrolmentRequest.setStatus(RequestStatusType.DENIED.getValue());
        }

        enrolmentRequestRepository.save(enrolmentRequest);
        return new ResponseEntity<>(new MessageResponse("Operation successful!"), HttpStatus.OK);

    }

    public List<EnrolmentRequest> getPendingUserEnrolmentRequestsByEmail(String email) {
        return enrolmentRequestRepository.getEnrolmentRequestsByEmailAndStatusType(email,
                RequestStatusType.PENDING.getValue());
    }

    public void saveEnrolmentRequest(EnrolmentRequest enrolmentRequest) {
        enrolmentRequestRepository.save(enrolmentRequest);
    }
}
