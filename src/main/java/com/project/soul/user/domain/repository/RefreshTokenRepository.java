package com.project.soul.user.domain.repository;

import com.project.soul.user.domain.entity.RefreshToken;
import com.project.soul.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteByUser(User user);
}
