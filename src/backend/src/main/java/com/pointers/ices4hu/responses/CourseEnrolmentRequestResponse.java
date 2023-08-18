package com.pointers.ices4hu.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseEnrolmentRequestResponse {
     private Long requestId;
     private CourseResponse course;
     private String studentName;
     private String studentSurname;
     private String studentEMail;
     private LocalDateTime requestTime;
}
