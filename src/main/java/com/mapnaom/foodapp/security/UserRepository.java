package com.mapnaom.foodapp.security;

import com.mapnaom.foodapp.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> , JpaSpecificationExecutor<User> {


    @Query("select u from User u where u.username = :username")
    Optional<User> findUserByUsername(@Param("username") String username);

    @Query("select (count(u) > 0) from User u where u.username = :username")
    boolean existsUserByUsername(@Param("username") String username);

    String username(String username);

    boolean existsByUsername(@NotBlank(message = "Username cannot be blank") @Size(min = 3, message = "Username must be at least 3 characters long") String username);

    @Query("select u from User u where u.employeeCode = :employeeCode")
    Optional<User> findByEmployeeCode(@Param("employeeCode") String employeeCode);
}
