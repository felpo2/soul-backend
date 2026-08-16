package com.project.soul.posts.domain.repository;

import com.project.soul.posts.domain.entity.Interaction;
import com.project.soul.posts.domain.entity.InteractionType;
import com.project.soul.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InteractionRepository extends JpaRepository<Interaction, UUID> {

    boolean existsByUserAndPostIdAndType(
            User user,
            UUID postId,
            InteractionType type
    );

    void deleteByUserAndPostIdAndType(
            User user,
            UUID postId,
            InteractionType type
    );

    long countByPostIdAndType(
            UUID postId,
            InteractionType type
    );
}