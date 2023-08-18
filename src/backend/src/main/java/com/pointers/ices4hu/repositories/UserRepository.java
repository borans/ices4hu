package com.pointers.ices4hu.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pointers.ices4hu.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByLoginID(String loginID);

    @Query("FROM User WHERE department.id=:departmentIdParam AND userType=:userTypeParam")
    List<User> findSpecificUsersAtDepartment(Long departmentIdParam, Byte userTypeParam);

}
