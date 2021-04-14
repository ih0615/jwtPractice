package com.test.jwt.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long>, UserCustom{
    @Query(value = "select u from User u where u.userId = :userId")
    User findByUserId(@Param("userId") String userId);
}
