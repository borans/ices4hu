package com.pointers.ices4hu.repositories;

import com.pointers.ices4hu.models.ReevaluationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReevaluationRequestRepository extends JpaRepository<ReevaluationRequest, Long> {

    @Query("FROM ReevaluationRequest R " +
            "inner join User U on R.userRequest.id=U.id " +
            "inner join Department D on U.department.id=D.id " +
            "where D.id=:departmentIdParam")
    List<ReevaluationRequest> findReevaluationRequestsAtDepartment(Long departmentIdParam);

    @Query("FROM ReevaluationRequest WHERE survey.id=:surveyIdParam")
    ReevaluationRequest findReevaluationRequestBySurveyId(Long surveyIdParam);

    @Query("FROM ReevaluationRequest WHERE userRequest.id=:userIdParam")
    List<ReevaluationRequest> findReevaluationRequestsByRequesterUserId(Long userIdParam);

    @Query("FROM ReevaluationRequest WHERE userEvaluate.id=:userIdParam")
    List<ReevaluationRequest> findReevaluationRequestsByEvaluatorUserId(Long userIdParam);

}
