package com.project.soul.user.application.service;

import com.project.soul.user.application.dto.LoginResponseDTO;
import com.project.soul.user.application.dto.UserResponseDTO;
import com.project.soul.user.application.exception.InvalidRefreshTokenException;
import com.project.soul.user.domain.entity.RefreshToken;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.domain.repository.RefreshTokenRepository;
import com.project.soul.user.infrastructure.security.JwtTokenService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class RefreshTokenService {

    private static final long EXPIRATION_DAYS = 30;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            JwtTokenService jwtTokenService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public String create(User user) {
        String rawToken = generateToken();
        Instant now = Instant.now();
        RefreshToken refreshToken = new RefreshToken(
                hash(rawToken),
                user,
                now,
                now.plus(EXPIRATION_DAYS, ChronoUnit.DAYS)
        );
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    @Transactional
    public LoginResponseDTO rotate(String rawToken) {
        RefreshToken currentToken = find(rawToken);
        if (!currentToken.isUsable()) {
            throw new InvalidRefreshTokenException("Refresh token is invalid or expired.");
        }

        currentToken.revoke();
        refreshTokenRepository.save(currentToken);

        User user = currentToken.getUser();
        String accessToken = jwtTokenService.generateToken(user);
        String newRefreshToken = create(user);
        return new LoginResponseDTO(accessToken, newRefreshToken, UserResponseDTO.from(user));
    }

    @Transactional
    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHash(hash(rawToken)).ifPresent(token -> {
            token.revoke();
            refreshTokenRepository.save(token);
        });
    }

    @Transactional
    public void revokeAll(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    private RefreshToken find(String rawToken) {
        return refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token is invalid or expired."));
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
