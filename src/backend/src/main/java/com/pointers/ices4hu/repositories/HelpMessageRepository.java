package com.pointers.ices4hu.repositories;

import com.pointers.ices4hu.models.HelpMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpMessageRepository extends JpaRepository<HelpMessage, Long> {

    @Query("FROM HelpMessage WHERE user.id=:userIdParam")
    List<HelpMessage> findHelpMessagesByUserId(Long userIdParam);

}
