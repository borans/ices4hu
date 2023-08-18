package com.pointers.ices4hu.repositories;

import com.pointers.ices4hu.models.StudentSurveyFill;
import com.pointers.ices4hu.models.ids.StudentSurveyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentSurveyFillRepository extends JpaRepository<StudentSurveyFill, StudentSurveyId> {
    @Query("FROM StudentSurveyFill WHERE user.loginID = :studentLoginIdParam AND survey.id = :surveyIdParam")
    StudentSurveyFill getStudentSurveyFillByStudentLoginIdAndSurveyId(String studentLoginIdParam, Long surveyIdParam);

    @Query("SELECT COUNT(*) FROM StudentSurveyFill WHERE survey.id = :surveyIdParam AND completionDatetime <> null")
    Long getNumberOfStudentsFilledTheSurvey(Long surveyIdParam);

    @Query("FROM StudentSurveyFill WHERE survey.id = :surveyIdParam AND completionDatetime <> null")
    List<StudentSurveyFill> getStudentSurveyFillsBySurveyId(Long surveyIdParam);

    @Query("FROM StudentSurveyFill WHERE user.id=:userIdParam")
    List<StudentSurveyFill> findStudentSurveyFillsByUserId(Long userIdParam);

    @Query("FROM StudentSurveyFill WHERE survey.id = :surveyIdParam AND completionDatetime is null")
    List<StudentSurveyFill> getUnsubmittedStudentSurveyFillsBySurveyId(Long surveyIdParam);

}
