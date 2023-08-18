package com.pointers.ices4hu.repositories;

import com.pointers.ices4hu.models.NewsletterPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsletterPostRepository extends JpaRepository<NewsletterPost, Long> {

    @Query("FROM NewsletterPost WHERE user.id=:userIdParam")
    List<NewsletterPost> findNewsletterPostsByUserId(Long userIdParam);

    @Query("FROM NewsletterPost WHERE department.id=:departmentIdParam")
    List<NewsletterPost> findNewsletterPostsByDepartmentId(Long departmentIdParam);

}
