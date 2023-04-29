package com.example.learning.spring.batch.repository;

import com.example.learning.spring.batch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository class using JpaRepository to perform CRUD on the data
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

//    @Query("UPDATE User u WHERE u.id IN (:userList)")
//    void customQueryCanBeDone(List<User> userList);
//
//    void deleteUserWithStatus(String status);
}
