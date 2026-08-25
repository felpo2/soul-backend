package com.project.soul.user.application.service;

import com.project.soul.user.application.dto.LoginResponseDTO;
import com.project.soul.user.application.exception.InvalidRefreshTokenException;
import com.project.soul.user.domain.entity.RefreshToken;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.domain.repository.RefreshTokenRepository;
import com.project.soul.user.infrastructure.security.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock JwtTokenService jwtTokenService;
    @InjectMocks RefreshTokenService refreshTokenService;

    @Test
    void createPersistsOnlyTokenHash() {
        User user = User.builder().username("felipe").build();

        String rawToken = refreshTokenService.create(user);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertNotEquals(rawToken, captor.getValue().getTokenHash());
        assertEquals(64, captor.getValue().getTokenHash().length());
        assertSame(user, captor.getValue().getUser());
    }

    @Test
    void rotateRevokesCurrentTokenAndReturnsNewPair() throws Exception {
        String rawToken = "current-refresh-token";
        User user = User.builder().username("felipe").build();
        RefreshToken current = new RefreshToken(
                hash(rawToken),
                user,
                Instant.now().minusSeconds(60),
                Instant.now().plusSeconds(3600)
        );
        when(refreshTokenRepository.findByTokenHash(hash(rawToken))).thenReturn(Optional.of(current));
        when(jwtTokenService.generateToken(user)).thenReturn("new-access-token");

        LoginResponseDTO response = refreshTokenService.rotate(rawToken);

        assertEquals("new-access-token", response.token());
        assertNotNull(response.refreshToken());
        assertNotEquals(rawToken, response.refreshToken());
        assertNotNull(current.getRevokedAt());
        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
    }

    @Test
    void rotateRejectsExpiredToken() throws Exception {
        String rawToken = "expired-refresh-token";
        User user = User.builder().build();
        RefreshToken expired = new RefreshToken(
                hash(rawToken),
                user,
                Instant.now().minusSeconds(7200),
                Instant.now().minusSeconds(3600)
        );
        when(refreshTokenRepository.findByTokenHash(hash(rawToken))).thenReturn(Optional.of(expired));

        assertThrows(InvalidRefreshTokenException.class, () -> refreshTokenService.rotate(rawToken));
        verifyNoInteractions(jwtTokenService);
    }

    private String hash(String token) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
    }
}
