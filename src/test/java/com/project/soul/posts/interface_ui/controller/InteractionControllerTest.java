package com.project.soul.posts.interface_ui.controller;

import com.project.soul.posts.application.service.InteractionService;
import com.project.soul.user.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InteractionControllerTest {

    @Test
    void likeUsesAuthenticatedUsersId() {
        InteractionService service = mock(InteractionService.class);
        InteractionController controller = new InteractionController(service);
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        var authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );

        controller.likePost(postId, authentication);

        verify(service).likePost(postId, userId);
    }
}
