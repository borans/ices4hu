package com.pointers.ices4hu.requests;

import lombok.Data;

@Data
public class AddInstructorRequest {
    private String name;
    private String surname;
    private String loginId;
    private String email;
    private Long departmentId;
}
