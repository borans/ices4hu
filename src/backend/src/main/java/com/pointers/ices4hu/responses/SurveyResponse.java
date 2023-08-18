package com.pointers.ices4hu.responses;

import com.pointers.ices4hu.models.Survey;
import com.pointers.ices4hu.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SurveyResponse {

    private Long id;
    private LocalDateTime creationDatetime;
    private LocalDateTime startingDatetime;
    private LocalDateTime deadline;
    private Integer trialCount;
    private Long courseId;
    private Long instructorId;
    private String courseName;
    private String courseCode;
    private String surveyType;
    private String instructorName;
    private String status;

    public void buildFrom(Survey survey) {

        setId(survey.getId());

        User instructor = null;

        if (survey.getCourse() != null) {
            setCourseId(survey.getCourse().getId());
            setCourseCode(survey.getCourse().getCode());
            setCourseName(survey.getCourse().getName());
            setSurveyType("Course");

            instructor = survey.getCourse().getUser();

        } else if (survey.getUserInstructor() != null) {
            setSurveyType("Instructor");
            instructor = survey.getUserInstructor();
        }

        setInstructorId(instructor.getId());
        setInstructorName(instructor.getFullName());

        setCreationDatetime(survey.getCreationDatetime());
        setDeadline(survey.getDeadline());
        setStartingDatetime(survey.getStartingDatetime());
        setTrialCount(survey.getTrialCount());

        if (survey.getCreationDatetime() == null) {
            setStatus("NOT SUBMITTED");
        } else {
            setStatus("SUBMITTED");
        }

    }

}
