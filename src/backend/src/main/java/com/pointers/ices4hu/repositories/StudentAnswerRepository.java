package com.pointers.ices4hu.repositories;

import com.pointers.ices4hu.models.StudentAnswer;
import com.pointers.ices4hu.models.ids.StudentQuestionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, StudentQuestionId> {

    @Query("FROM StudentAnswer WHERE user.id = :studentIdParam AND question.id = :questionIdParam")
    StudentAnswer findStudentAnswerByStudentAndQuestionIds(Long studentIdParam, Long questionIdParam);

    @Query("FROM StudentAnswer WHERE user.id=:userIdParam")
    List<StudentAnswer> findStudentAnswersByUserId(Long userIdParam);

}
