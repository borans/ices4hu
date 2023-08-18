package com.pointers.ices4hu.responses;

import com.pointers.ices4hu.models.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseResponse {
    private Long id;
    private String department;
    private String code;
    private String name;
    private String courseType;
    private int credit;
    private String courseDegree;
    private String instructorName;
    private String evaluationFormStatus;

    /*
     * This method does not set evaluationFromStatus,
     * caller must handle it.
     */
    public void buildFrom(Course course) {
        setId(course.getId());
        setDepartment(course.getDepartment().getName());

        setCode(course.getCode());
        setName(course.getName());

        if (course.getMandatory())
            setCourseType("Mandatory");
        else
            setCourseType("Elective");

        setCredit(course.getCredit());

        if (course.getUndergraduate())
            setCourseDegree("Undergraduate");
        else
            setCourseDegree("Graduate");

        if (course.getUser() != null)
            setInstructorName(course.getUser().getFullName());
    }
}
