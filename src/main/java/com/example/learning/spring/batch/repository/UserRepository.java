package com.example.learning.spring.batch.repository;

import com.example.learning.spring.batch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository class using JpaRepository to perform CRUD on the data
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
