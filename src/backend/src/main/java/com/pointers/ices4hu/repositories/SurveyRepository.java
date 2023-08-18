package com.pointers.ices4hu.repositories;


import com.pointers.ices4hu.models.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    @Query("FROM Survey WHERE course.id = :courseIdParam")
    List<Survey> findByCourseId(@Param("courseIdParam") Long courseId);

    @Query("FROM Survey WHERE userInstructor.id = :instructorIdParam")
    List<Survey> findByInstructorId(@Param("instructorIdParam") Long instructorId);

    @Query("FROM Survey WHERE userCreator.id = :creatorIdParam")
    List<Survey> findByCreatorIntegerId(@Param("creatorIdParam") Long creatorIdParam);

    @Query("FROM Survey WHERE userCreator.loginID = :creatorLoginIdParam")
    List<Survey> findByCreatorId(@Param("creatorLoginIdParam") String creatorLoginIdParam);

    @Query("FROM Survey WHERE (course <> null AND course.department.id = :departmentIdParam)")
    List<Survey> findCourseSurveysByDepartmentId(Long departmentIdParam);

    @Query("FROM Survey WHERE (userInstructor <> null AND userInstructor.department.id = :departmentIdParam)")
    List<Survey> findInstructorSurveysByDepartmentId(Long departmentIdParam);

}
