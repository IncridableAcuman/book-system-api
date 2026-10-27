package com.app.backend.util;

import com.app.backend.entity.User;
import com.app.backend.enums.Role;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User testUser;
    private static final String SECRET = "mySuperSecretKeyForTestingPurposesMustBeAtLeast256BitsLong12345";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "accessTime", 60000); // 1 minute
        ReflectionTestUtils.setField(jwtUtil, "refreshTime", 120000); // 2 minutes
        ReflectionTestUtils.setField(jwtUtil, "resetTime", 30000); // 30 seconds
        jwtUtil.init();

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole(Role.USER);
    }

    @Test
    @DisplayName("Should generate valid access token containing user claims")
    void testGetAccessToken() {
        String token = jwtUtil.getAccessToken(testUser);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.extractSubject(token)).isEqualTo("test@example.com");

        Claims claims = jwtUtil.extractClaim(token);
        assertThat(claims.get("id", Long.class)).isEqualTo(1L);
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should generate valid refresh token")
    void testGetRefreshToken() {
        String token = jwtUtil.getRefreshToken(testUser);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.extractSubject(token)).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should generate valid reset token")
    void testGenerateResetToken() {
        String token = jwtUtil.generateResetToken(testUser);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.extractSubject(token)).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should correctly extract expiration date")
    void testExtractExpiration() {
        String token = jwtUtil.getAccessToken(testUser);
        Date expiration = jwtUtil.extractExpiration(token);

        assertThat(expiration).isNotNull();
        assertThat(expiration).isAfter(new Date());
    }

    @Test
    @DisplayName("Should return false for invalid or malformed token")
    void testValidateToken_Invalid() {
        assertThat(jwtUtil.validateToken("invalid.token.here")).isFalse();
        assertThat(jwtUtil.validateToken(null)).isFalse();
        assertThat(jwtUtil.validateToken("")).isFalse();
    }

    @Test
    @DisplayName("Should return false for expired token")
    void testValidateToken_Expired() {
        // Generate token with negative expiry time to simulate expired token
        String expiredToken = jwtUtil.generateToken(testUser, -1000);

        assertThat(jwtUtil.validateToken(expiredToken)).isFalse();
    }
}
