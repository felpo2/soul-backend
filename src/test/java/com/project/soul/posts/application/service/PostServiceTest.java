package com.project.soul.posts.application.service;

import com.project.soul.posts.application.dto.PostRequestDTO;
import com.project.soul.posts.domain.entity.Post;
import com.project.soul.posts.domain.repository.PostRepository;
import com.project.soul.posts.domain.repository.CommentRepository;
import com.project.soul.posts.domain.repository.InteractionRepository;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.domain.entity.FollowStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock PostRepository postRepository;
    @Mock CommentRepository commentRepository;
    @Mock InteractionRepository interactionRepository;
    @InjectMocks PostService postService;

    @Test
    void userCannotEditAnotherUsersPost() {
        User owner = User.builder().id(UUID.randomUUID()).build();
        User attacker = User.builder().id(UUID.randomUUID()).build();
        UUID postId = UUID.randomUUID();
        Post post = Post.builder().id(postId).user(owner).build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(AccessDeniedException.class, () -> postService.updatePost(
                postId,
                attacker,
                new PostRequestDTO("changed", null)
        ));
        verify(postRepository, never()).save(any());
    }

    @Test
    void feedUsesAcceptedRelationships() {
        User user = User.builder().id(UUID.randomUUID()).build();
        Post post = Post.builder().id(UUID.randomUUID()).user(user).build();
        PageRequest pageable = PageRequest.of(0, 20);
        when(postRepository.findFeed(user.getId(), FollowStatus.ACCEPTED, pageable))
                .thenReturn(new PageImpl<>(List.of(post)));

        var result = postService.listFeed(user, pageable);

        assertEquals(1, result.getTotalElements());
        verify(postRepository).findFeed(user.getId(), FollowStatus.ACCEPTED, pageable);
    }
}
