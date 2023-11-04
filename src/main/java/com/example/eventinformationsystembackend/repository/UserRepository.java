package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET is_enabled = true WHERE username = :username",
           nativeQuery = true)
    void updateIsEnabledColumn(@Param("username") String username);
}
