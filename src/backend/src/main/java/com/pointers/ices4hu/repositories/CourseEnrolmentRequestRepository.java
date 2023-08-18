package com.pointers.ices4hu.repositories;

import com.pointers.ices4hu.models.CourseEnrolmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseEnrolmentRequestRepository extends JpaRepository<CourseEnrolmentRequest, Long> {

    @Query("FROM CourseEnrolmentRequest WHERE course.id=:courseIdParam AND user.id=:studentIdParam " +
            "AND status <> :statusTypeParam")
    List<CourseEnrolmentRequest> getCourseEnrolmentRequestsByCourseAndStudentIds(Long courseIdParam,
                                                                          Long studentIdParam,
                                                                          Byte statusTypeParam);

    @Query("FROM CourseEnrolmentRequest WHERE course.id=:courseIdParam AND user.id=:studentIdParam " +
            "AND status=:statusTypeParam")
    List<CourseEnrolmentRequest> getCourseEnrolmentRequestsByCourseAndStudentIdsWithStatusType(Long courseIdParam,
                                                                          Long studentIdParam,
                                                                          Byte statusTypeParam);

    @Query("FROM CourseEnrolmentRequest WHERE status=:statusTypeParam")
    List<CourseEnrolmentRequest> getCourseEnrolmentRequestsByStatusType(Byte statusTypeParam);

    @Query("FROM CourseEnrolmentRequest WHERE course.id=:courseIdParam")
    List<CourseEnrolmentRequest> getCourseEnrolmentRequestsByCourseId(Long courseIdParam);

    @Query("FROM CourseEnrolmentRequest WHERE user.id=:userIdParam")
    List<CourseEnrolmentRequest> getCourseEnrolmentRequestsByUserId(Long userIdParam);

}
