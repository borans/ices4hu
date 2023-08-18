package com.pointers.ices4hu.responses;

import com.pointers.ices4hu.models.Department;
import lombok.Data;

@Data
public class DepartmentResponse {
    private Long id;
    private String name;

    public void buildFrom(Department department) {
        setId(department.getId());
        setName(department.getName());
    }

}
