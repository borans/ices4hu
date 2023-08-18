package com.pointers.ices4hu.repositories;

import com.pointers.ices4hu.models.QuestionBank;
import com.pointers.ices4hu.models.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {

    @Query("FROM QuestionBank WHERE user.loginID = :instructorLoginIdParam")
    QuestionBank findQuestionBankByInstructorLoginId(String instructorLoginIdParam);

}
