package com.app.backend.service;

import com.app.backend.entity.RefreshToken;
import com.app.backend.entity.User;
import com.app.backend.exception.BadRequestException;
import com.app.backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh_time}")
    private long refreshTime;

    /**
     * Saves or updates the refresh token in the database for the given user.
     * Ensures one active refresh token record per user with automatic expiration.
     */
    @Transactional
    public void saveToken(User user, String refreshToken) {
        if (user == null || user.getId() == null) {
            throw new BadRequestException("User or User ID is null");
        }
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException("Token is empty or null");
        }

        Instant expiryDate = Instant.now().plusMillis(refreshTime);

        Optional<RefreshToken> existingOpt = refreshTokenRepository.findByUserId(user.getId());
        RefreshToken tokenEntity;
        if (existingOpt.isPresent()) {
            tokenEntity = existingOpt.get();
            tokenEntity.setToken(refreshToken);
            tokenEntity.setExpiryDate(expiryDate);
            tokenEntity.setRevoked(false);
        } else {
            tokenEntity = RefreshToken.builder()
                    .user(user)
                    .token(refreshToken)
                    .expiryDate(expiryDate)
                    .createdAt(Instant.now())
                    .revoked(false)
                    .build();
        }
        refreshTokenRepository.save(tokenEntity);
    }

    /**
     * Retrieves the active, non-expired refresh token for a user from the database.
     */
    @Transactional(readOnly = true)
    public String get(Long userId) {
        if (userId == null) {
            return null;
        }
        return refreshTokenRepository.findByUserId(userId)
                .filter(RefreshToken::isValid)
                .map(RefreshToken::getToken)
                .orElse(null);
    }

    /**
     * Deletes the refresh token record for a user upon logout or revocation.
     */
    @Transactional
    public void delete(Long userId) {
        if (userId == null) {
            return;
        }
        refreshTokenRepository.deleteByUserId(userId);
    }

    /**
     * Finds a refresh token entity by its raw string.
     */
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Production maintenance: automatically purges expired tokens from the database.
     * Default schedule runs daily at 03:00 AM.
     */
    @Scheduled(cron = "${jwt.cleanup_cron:0 0 3 * * ?}")
    @Transactional
    public void purgeExpiredTokens() {
        int deleted = refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
        if (deleted > 0) {
            log.info("Purged {} expired refresh token(s) from database", deleted);
        }
    }
}
