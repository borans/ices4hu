package com.pointers.ices4hu.repositories;

import com.pointers.ices4hu.models.MultipleChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultipleChoiceRepository extends JpaRepository<MultipleChoice, Long> {

    @Query("FROM MultipleChoice WHERE question.id = :questionIdParam")
    List<MultipleChoice> findMultipleChoicesByQuestionId(Long questionIdParam);

}
