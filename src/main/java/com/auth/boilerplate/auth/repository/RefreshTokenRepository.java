package com.auth.boilerplate.auth.repository;

import com.auth.boilerplate.auth.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByUserId(Long userId);
    Optional<RefreshToken> findByUserId(Long userId);
}
