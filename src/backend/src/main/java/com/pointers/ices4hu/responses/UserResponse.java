package com.pointers.ices4hu.responses;

import com.pointers.ices4hu.models.User;
import com.pointers.ices4hu.types.StudentDegree;
import com.pointers.ices4hu.types.UserType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String loginId;
    private String name;
    private String surname;
    private String email;
    private String userType;
    private String degree;
    private String departmentName;
    private LocalDateTime registrationTime;

    public void buildFrom(User user) {
        if (user == null)
            return;

        setId(user.getId());
        setLoginId(user.getLoginID());
        setName(user.getName());
        setSurname(user.getSurname());
        setEmail(user.getEmail());

        setUserType(UserType.values()[user.getUserType().intValue()].getName());
        if (user.getDegree() != null)
            setDegree(StudentDegree.values()[user.getDegree().intValue()].getName());

        if (user.getDepartment() != null)
            setDepartmentName(user.getDepartment().getName());

        setRegistrationTime(user.getRegistrationDateTime());
    }
}
