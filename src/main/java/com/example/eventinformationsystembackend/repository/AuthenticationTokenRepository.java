package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.model.AuthenticationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationTokenRepository extends JpaRepository<AuthenticationToken, Long> {
}
