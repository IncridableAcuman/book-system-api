package com.app.backend.service;

import com.app.backend.entity.User;
import com.app.backend.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private RedisTemplate<String, Object> template;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private TokenService tokenService;

    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "refreshTime", 60000);
        user = new User();
        user.setId(42L);
        user.setEmail("user@example.com");
    }

    @Test
    @DisplayName("Should successfully save token into redis cache")
    void testSaveToken_Success() {
        when(template.opsForValue()).thenReturn(valueOperations);

        tokenService.saveToken(user, "valid-refresh-token");

        verify(valueOperations, times(1)).set("refreshToken:42", "valid-refresh-token", 60000, TimeUnit.MILLISECONDS);
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
    }

    @Test
    @DisplayName("Should return null when get token with null userId")
    void testGet_NullUserId() {
        String token = tokenService.get(null);
        assertThat(token).isNull();
        verifyNoInteractions(template);
    }

    @Test
    @DisplayName("Should return token when found in cache")
    void testGet_Found() {
        when(template.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refreshToken:42")).thenReturn("cached-token");

        String token = tokenService.get(42L);

        assertThat(token).isEqualTo("cached-token");
        verify(valueOperations).get("refreshToken:42");
    }

    @Test
    @DisplayName("Should return null when token not found in cache")
    void testGet_NotFound() {
        when(template.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refreshToken:42")).thenReturn(null);

        String token = tokenService.get(42L);

        assertThat(token).isNull();
    }

    @Test
    @DisplayName("Should delete token from cache")
    void testDelete_Success() {
        tokenService.delete(42L);

        verify(template, times(1)).delete("refreshToken:42");
    }

    @Test
    @DisplayName("Should do nothing when delete is called with null userId")
    void testDelete_NullUserId() {
        tokenService.delete(null);

        verifyNoInteractions(template);
    }

    @Test
    @DisplayName("Should generate correct redis key format")
    void testGetKey() {
        String key = tokenService.getKey(100L);
        assertThat(key).isEqualTo("refreshToken:100");
    }
}
