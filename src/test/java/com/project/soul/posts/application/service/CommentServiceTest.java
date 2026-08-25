package com.project.soul.posts.application.service;

import com.project.soul.posts.application.dto.CommentRequestDTO;
import com.project.soul.posts.application.dto.CommentResponseDTO;
import com.project.soul.posts.domain.entity.Comment;
import com.project.soul.posts.domain.entity.Post;
import com.project.soul.posts.domain.repository.CommentRepository;
import com.project.soul.posts.domain.repository.PostRepository;
import com.project.soul.user.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock CommentRepository commentRepository;
    @Mock PostRepository postRepository;
    @InjectMocks CommentService commentService;

    @Test
    void createAssociatesAuthenticatedUserAndPost() {
        UUID postId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).username("felipe").build();
        Post post = Post.builder().id(postId).build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommentResponseDTO response = commentService.create(
                postId,
                user,
                new CommentRequestDTO("First comment")
        );

        assertEquals(postId, response.postId());
        assertEquals(user.getId(), response.userId());
        assertEquals("First comment", response.content());
    }

    @Test
    void userCannotUpdateAnotherUsersComment() {
        UUID postId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        User owner = User.builder().id(UUID.randomUUID()).build();
        User attacker = User.builder().id(UUID.randomUUID()).build();
        Post post = Post.builder().id(postId).build();
        Comment comment = new Comment(owner, post, "Original", java.time.Instant.now());
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(AccessDeniedException.class, () -> commentService.update(
                postId,
                commentId,
                attacker,
                new CommentRequestDTO("Changed")
        ));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void ownerCanDeleteComment() {
        UUID postId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        User owner = User.builder().id(UUID.randomUUID()).build();
        Post post = Post.builder().id(postId).build();
        Comment comment = new Comment(owner, post, "Comment", java.time.Instant.now());
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.delete(postId, commentId, owner);

        verify(commentRepository).delete(comment);
    }
}
