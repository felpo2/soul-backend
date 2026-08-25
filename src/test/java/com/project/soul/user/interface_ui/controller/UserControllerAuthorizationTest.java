package com.project.soul.user.interface_ui.controller;

import com.project.soul.user.application.dto.UpdateUserRequestDTO;
import com.project.soul.user.application.service.UserService;
import com.project.soul.user.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

class UserControllerAuthorizationTest {

    @Test
    void authenticatedUserCannotUpdateAnotherAccount() {
        UserService userService = mock(UserService.class);
        UserController controller = new UserController();
        controller.userService = userService;

        User authenticatedUser = User.builder().id(UUID.randomUUID()).build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticatedUser,
                null,
                authenticatedUser.getAuthorities()
        );

        assertThrows(AccessDeniedException.class, () -> controller.updateUser(
                UUID.randomUUID(),
                new UpdateUserRequestDTO("New name", null, null),
                authentication
        ));
        verifyNoInteractions(userService);
    }

    @Test
    void updateCurrentUserUsesAuthenticatedUsersId() {
        UserService userService = mock(UserService.class);
        UserController controller = new UserController();
        controller.userService = userService;
        UUID userId = UUID.randomUUID();
        User authenticatedUser = User.builder().id(userId).build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticatedUser,
                null,
                authenticatedUser.getAuthorities()
        );
        UpdateUserRequestDTO request = new UpdateUserRequestDTO("New name", null, null);
        when(userService.updateUser(userId, request)).thenReturn(authenticatedUser);

        controller.updateCurrentUser(request, authentication);

        verify(userService).updateUser(userId, request);
    }
}
