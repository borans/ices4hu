package com.pointers.ices4hu.responses;

import com.pointers.ices4hu.models.User;
import lombok.Data;

@Data
public class InstructorResponse {
    private Long id;
    private String name;

    public void buildFrom(User instructor) {
        this.id = instructor.getId();
        this.name = instructor.getFullName();
    }
}
