package com.pointers.ices4hu.repositories;

import com.pointers.ices4hu.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("FROM Question WHERE questionBank.id = :questionBankIdParam")
    List<Question> findQuestionsByQuestionBankId(Long questionBankIdParam);

    @Query("FROM Question WHERE survey.id = :surveyIdParam")
    List<Question> findQuestionsBySurveyId(Long surveyIdParam);

}
