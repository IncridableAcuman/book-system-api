package com.app.backend.service;

import com.app.backend.dto.AuthResponse;
import com.app.backend.entity.User;
import com.app.backend.enums.Role;
import com.app.backend.exception.UnauthorizeException;
import com.app.backend.util.CookieUtil;
import com.app.backend.util.JwtUtil;
import com.app.backend.util.MailUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenFacadeTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private TokenService tokenService;

    @Mock
    private MailUtil mailUtil;

    @InjectMocks
    private TokenFacade tokenFacade;

    private User user;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(7L);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setRole(Role.USER);

        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("Should issue tokens, store in database, set cookie, and return AuthResponse")
    void testIssueTokens() {
        when(jwtUtil.getAccessToken(user)).thenReturn("access-token-123");
        when(jwtUtil.getRefreshToken(user)).thenReturn("refresh-token-456");

        AuthResponse authResponse = tokenFacade.issueTokens(user, response);

        assertThat(authResponse).isNotNull();
        assertThat(authResponse.id()).isEqualTo(7L);
        assertThat(authResponse.email()).isEqualTo("john@example.com");
        assertThat(authResponse.accessToken()).isEqualTo("access-token-123");
        assertThat(authResponse.role()).isEqualTo(Role.USER);

        verify(tokenService).saveToken(user, "refresh-token-456");
        verify(cookieUtil).addCookie("refresh-token-456", response);
    }

    @Test
    @DisplayName("Should extract subject from token using JwtUtil")
    void testExtractSubjectFromToken() {
        when(jwtUtil.extractSubject("sample-token")).thenReturn("john@example.com");

        String subject = tokenFacade.extractSubjectFromToken("sample-token");

        assertThat(subject).isEqualTo("john@example.com");
        verify(jwtUtil).extractSubject("sample-token");
    }

    @Test
    @DisplayName("Should validate token successfully when valid")
    void testValidateToken_Valid() {
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);

        tokenFacade.validateToken("valid-token");

        verify(jwtUtil).validateToken("valid-token");
    }

    @Test
    @DisplayName("Should throw UnauthorizeException when token is invalid or expired")
    void testValidateToken_Invalid() {
        when(jwtUtil.validateToken("invalid-token")).thenReturn(false);

        assertThatThrownBy(() -> tokenFacade.validateToken("invalid-token"))
                .isInstanceOf(UnauthorizeException.class)
                .hasMessage("Token is invalid or expired");
    }

    @Test
    @DisplayName("Should delete token from database and clear cookie")
    void testDeleteTokenAndClearCookie() {
        tokenFacade.deleteTokenAndClearCookie(user, response);

        verify(tokenService).delete(7L);
        verify(cookieUtil).clearCookie(response);
    }

    @Test
    @DisplayName("Should send reset password email with generated token")
    void testResetToken() {
        when(jwtUtil.getAccessToken(user)).thenReturn("reset-token-xyz");

        tokenFacade.resetToken(user);

        verify(mailUtil).sendMail(
                "john@example.com",
                "Reset Password",
                "http://localhost:5173/reset-password?token=reset-token-xyz"
        );
    }
}
