package com.skillswap.repository;

import com.skillswap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username AND u.deletedAt IS NULL")
    boolean existsActiveUserByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.deletedAt IS NULL AND u.username = :username")
    Optional<User> findActiveByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.deletedAt IS NULL AND u.email = :email")
    Optional<User> findActiveByEmail(@Param("email") String email);
}
