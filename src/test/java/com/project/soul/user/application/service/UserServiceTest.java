package com.project.soul.user.application.service;

import com.project.soul.user.application.dto.LoginRequestDTO;
import com.project.soul.user.application.dto.LoginResponseDTO;
import com.project.soul.user.domain.entity.PasswordResetToken;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.domain.repository.TokenRepository;
import com.project.soul.user.domain.repository.UserRepository;
import com.project.soul.user.infrastructure.security.JwtTokenService;
import com.project.soul.posts.domain.repository.CommentRepository;
import com.project.soul.posts.domain.repository.InteractionRepository;
import com.project.soul.posts.domain.repository.PostRepository;
import com.project.soul.user.domain.repository.FollowRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock TokenRepository tokenRepository;
    @Mock EmailService emailService;
    @Mock JwtTokenService jwtTokenService;
    @Mock RefreshTokenService refreshTokenService;
    @Mock CommentRepository commentRepository;
    @Mock InteractionRepository interactionRepository;
    @Mock PostRepository postRepository;
    @Mock FollowRepository followRepository;

    @InjectMocks UserService userService;

    @Test
    void createUserEncryptsPasswordAndSetsServerManagedFields() {
        User user = User.builder()
                .username("felipe")
                .email("felipe@example.com")
                .password("plain-password")
                .build();

        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.createUser(user);

        assertEquals("encoded-password", created.getPassword());
        assertTrue(created.getAccountStatus());
        assertNotNull(created.getCreatedAt());
        verify(userRepository).save(user);
    }

    @Test
    void requestPasswordResetPersistsInitializedToken() {
        User user = User.builder().email("felipe@example.com").build();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        userService.requestPasswordReset(user.getEmail());

        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(captor.capture());

        PasswordResetToken token = captor.getValue();
        assertNotNull(token.getToken());
        assertSame(user, token.getUser());
        assertTrue(token.getExpiryDate().isAfter(Instant.now()));
        verify(emailService).sendResetPasswordEmail(user.getEmail(), token.getToken());
    }

    @Test
    void loginReturnsTokenAndPublicUserData() {
        User user = User.builder()
                .username("felipe")
                .email("felipe@example.com")
                .password("encoded-password")
                .build();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain-password", user.getPassword())).thenReturn(true);
        when(jwtTokenService.generateToken(user)).thenReturn("jwt-token");
        when(refreshTokenService.create(user)).thenReturn("refresh-token");

        LoginResponseDTO response = userService.realizeLogin(
                new LoginRequestDTO(user.getEmail(), "plain-password")
        );

        assertEquals("jwt-token", response.token());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals("felipe", response.user().username());
    }
}
