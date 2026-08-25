package com.project.soul.user.domain.repository;

import com.project.soul.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameIgnoreCase(String username);

    Page<User> findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(
            String username,
            String name,
            Pageable pageable
    );

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
