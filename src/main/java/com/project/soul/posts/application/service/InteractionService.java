package com.project.soul.posts.application.service;

import com.project.soul.posts.application.exception.InteractionAlreadyExistsException;
import com.project.soul.posts.application.exception.PostNotFoundException;
import com.project.soul.posts.domain.entity.Interaction;
import com.project.soul.posts.domain.entity.InteractionType;
import com.project.soul.posts.domain.entity.Post;
import com.project.soul.posts.domain.repository.InteractionRepository;
import com.project.soul.posts.domain.repository.PostRepository;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public InteractionService(
            InteractionRepository interactionRepository,
            PostRepository postRepository,
            UserRepository userRepository
    ) {
        this.interactionRepository = interactionRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void likePost(UUID postId, UUID userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (interactionRepository.existsByUserAndPostIdAndType(
                user,
                postId,
                InteractionType.LIKE
        )) {
            throw new InteractionAlreadyExistsException(
                    "User already liked this post."
            );
        }

        Interaction interaction = new Interaction(
                null,
                user,
                post,
                InteractionType.LIKE,
                null,
                LocalDateTime.now()
        );

        interactionRepository.save(interaction);
    }

    @Transactional
    public void unlikePost(UUID postId, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        interactionRepository.deleteByUserAndPostIdAndType(
                user,
                postId,
                InteractionType.LIKE
        );
    }

    @Transactional(readOnly = true)
    public long countLikes(UUID postId) {

        return interactionRepository.countByPostIdAndType(
                postId,
                InteractionType.LIKE
        );
    }
}