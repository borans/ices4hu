package com.pointers.ices4hu.repositories;

import com.pointers.ices4hu.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("FROM Course WHERE user.loginID = :instructorLoginIdParam")
    List<Course> findCoursesByInstructorLoginId(String instructorLoginIdParam);

    @Query("From Course WHERE user.id = :userIdParam")
    List<Course> findCoursesByUserId(Long userIdParam);

    @Query("FROM Course WHERE department.id = :departmentIdParam")
    List<Course> findCoursesByDepartmentId(Long departmentIdParam);

    @Query("FROM Course C " +
            "left outer join CourseEnrolmentRequest CER on C.id=CER.course.id " +
            "WHERE ((CER.user.id=:studentIdParam AND CER.status=:statusTypeParam) OR CER.user.id is null) " +
            "AND C.department.id=:departmentIdParam")
    List<Course> findCoursesForCourseRegistration(Long studentIdParam, Byte statusTypeParam, Long departmentIdParam);

    @Query("FROM Course WHERE code=:codeParam")
    List<Course> findCoursesByCode(String codeParam);

}
