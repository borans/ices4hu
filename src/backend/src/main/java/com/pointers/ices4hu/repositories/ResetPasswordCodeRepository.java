package com.pointers.ices4hu.repositories;

import com.pointers.ices4hu.models.ResetPasswordCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordCodeRepository extends JpaRepository<ResetPasswordCode, Long> {

    @Query("FROM ResetPasswordCode WHERE loginId=:loginIdParam")
    ResetPasswordCode findByLoginId(String loginIdParam);

}
