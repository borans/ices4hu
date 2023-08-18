package com.pointers.ices4hu.requests;

import lombok.Data;

@Data
public class SignUpRequest {
    private String name;
    private String surname;
    private String email;
    private Long departmentId;
    private Byte degree;

    private Byte userType;

}
