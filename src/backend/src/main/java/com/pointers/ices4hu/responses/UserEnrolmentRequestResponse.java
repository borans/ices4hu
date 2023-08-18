package com.pointers.ices4hu.responses;

import com.pointers.ices4hu.models.EnrolmentRequest;
import com.pointers.ices4hu.types.UserType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserEnrolmentRequestResponse {
    private Long requestId;
    private String accountType;
    private String name;
    private String surname;
    private String email;
    private String department;
    private LocalDateTime requestTime;

    public void buildFrom(EnrolmentRequest enrolmentRequest) {
        setRequestId(enrolmentRequest.getRequestId());
        setName(enrolmentRequest.getName());
        setSurname(enrolmentRequest.getSurname());
        setEmail(enrolmentRequest.getEmail());

        if (enrolmentRequest.getDepartment() != null) {
            setDepartment(enrolmentRequest.getDepartment().getName());
        }

        setRequestTime(enrolmentRequest.getRequestDateTime());

        String accountType = UserType.values()[enrolmentRequest.getUserType().intValue()].getName();
        setAccountType(accountType);

    }

}
