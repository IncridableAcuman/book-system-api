package com.app.backend.service;

import com.app.backend.entity.RefreshToken;
import com.app.backend.entity.User;
import com.app.backend.exception.BadRequestException;
import com.app.backend.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private TokenService tokenService;

    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "refreshTime", 60000L);
        user = new User();
        user.setId(42L);
        user.setEmail("user@example.com");
    }

    @Test
    @DisplayName("Should successfully insert new token into database when none exists")
    void testSaveToken_NewToken() {
        when(refreshTokenRepository.findByUserId(42L)).thenReturn(Optional.empty());

        tokenService.saveToken(user, "valid-refresh-token");

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());

        RefreshToken saved = captor.getValue();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getToken()).isEqualTo("valid-refresh-token");
        assertThat(saved.isRevoked()).isFalse();
        assertThat(saved.getExpiryDate()).isAfter(Instant.now());
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update existing token in database when user already has a token record")
    void testSaveToken_UpdateExisting() {
        RefreshToken existing = RefreshToken.builder()
                .id(1L)
                .user(user)
                .token("old-token")
                .expiryDate(Instant.now().minusSeconds(10))
                .createdAt(Instant.now().minusSeconds(3600))
                .revoked(true)
                .build();

        when(refreshTokenRepository.findByUserId(42L)).thenReturn(Optional.of(existing));

        tokenService.saveToken(user, "updated-refresh-token");

        verify(refreshTokenRepository).save(existing);
        assertThat(existing.getToken()).isEqualTo("updated-refresh-token");
        assertThat(existing.isRevoked()).isFalse();
        assertThat(existing.getExpiryDate()).isAfter(Instant.now());
    }

    @Test
    @DisplayName("Should throw BadRequestException when user is null on save")
    void testSaveToken_NullUser() {
        assertThatThrownBy(() -> tokenService.saveToken(null, "some-token"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("User or User ID is null");
    }

    @Test
    @DisplayName("Should throw BadRequestException when user id is null on save")
    void testSaveToken_NullUserId() {
        User userWithoutId = new User();
        assertThatThrownBy(() -> tokenService.saveToken(userWithoutId, "some-token"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("User or User ID is null");
    }

    @Test
    @DisplayName("Should throw BadRequestException when token is null or empty on save")
    void testSaveToken_NullOrEmptyToken() {
        assertThatThrownBy(() -> tokenService.saveToken(user, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Token is empty or null");

        assertThatThrownBy(() -> tokenService.saveToken(user, ""))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Token is empty or null");

        assertThatThrownBy(() -> tokenService.saveToken(user, "   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Token is empty or null");
    }

    @Test
    @DisplayName("Should return null when get token with null userId")
    void testGet_NullUserId() {
        String token = tokenService.get(null);
        assertThat(token).isNull();
        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    @DisplayName("Should return token when found in database and still valid")
    void testGet_FoundAndValid() {
        RefreshToken validToken = RefreshToken.builder()
                .id(1L)
                .user(user)
                .token("active-token")
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByUserId(42L)).thenReturn(Optional.of(validToken));

        String token = tokenService.get(42L);

        assertThat(token).isEqualTo("active-token");
        verify(refreshTokenRepository).findByUserId(42L);
    }

    @Test
    @DisplayName("Should return null when token exists but has expired")
    void testGet_Expired() {
        RefreshToken expiredToken = RefreshToken.builder()
                .id(1L)
                .user(user)
                .token("expired-token")
                .expiryDate(Instant.now().minusSeconds(10))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByUserId(42L)).thenReturn(Optional.of(expiredToken));

        String token = tokenService.get(42L);

        assertThat(token).isNull();
    }

    @Test
    @DisplayName("Should return null when token exists but has been revoked")
    void testGet_Revoked() {
        RefreshToken revokedToken = RefreshToken.builder()
                .id(1L)
                .user(user)
                .token("revoked-token")
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(true)
                .build();

        when(refreshTokenRepository.findByUserId(42L)).thenReturn(Optional.of(revokedToken));

        String token = tokenService.get(42L);

        assertThat(token).isNull();
    }

    @Test
    @DisplayName("Should return null when token not found in database")
    void testGet_NotFound() {
        when(refreshTokenRepository.findByUserId(42L)).thenReturn(Optional.empty());

        String token = tokenService.get(42L);

        assertThat(token).isNull();
    }

    @Test
    @DisplayName("Should delete token from database by userId")
    void testDelete_Success() {
        tokenService.delete(42L);

        verify(refreshTokenRepository, times(1)).deleteByUserId(42L);
    }

    @Test
    @DisplayName("Should do nothing when delete is called with null userId")
    void testDelete_NullUserId() {
        tokenService.delete(null);

        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    @DisplayName("Should purge expired tokens older than current timestamp")
    void testPurgeExpiredTokens() {
        when(refreshTokenRepository.deleteByExpiryDateBefore(any(Instant.class))).thenReturn(5);

        tokenService.purgeExpiredTokens();

        verify(refreshTokenRepository).deleteByExpiryDateBefore(any(Instant.class));
    }
}
