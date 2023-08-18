package com.pointers.ices4hu.requests;

import lombok.Data;

@Data
public class AddCourseRequest {
    private String name;
    private String code;
    private Long departmentId;
    private Integer credit;
    private Boolean undergraduate;
    private Boolean mandatory;
}
