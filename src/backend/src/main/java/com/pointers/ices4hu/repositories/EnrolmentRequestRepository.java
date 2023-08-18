package com.pointers.ices4hu.repositories;

import com.pointers.ices4hu.models.EnrolmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrolmentRequestRepository extends JpaRepository<EnrolmentRequest, Long> {

    @Query("FROM EnrolmentRequest WHERE status=:statusTypeParam")
    List<EnrolmentRequest> getEnrolmentRequestsByStatusType(Byte statusTypeParam);

    @Query("FROM EnrolmentRequest WHERE user.id=:userIdParam")
    List<EnrolmentRequest> getEnrolmentRequestsByUserId(Long userIdParam);

    @Query("FROM EnrolmentRequest WHERE email=:emailParam AND status=:statusTypeParam")
    List<EnrolmentRequest> getEnrolmentRequestsByEmailAndStatusType(String emailParam, Byte statusTypeParam);

}
