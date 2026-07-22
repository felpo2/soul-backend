package com.project.soul.user.domain.repository;

import com.project.soul.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
