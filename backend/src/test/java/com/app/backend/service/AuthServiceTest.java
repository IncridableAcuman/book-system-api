package com.app.backend.service;

import com.app.backend.dto.*;
import com.app.backend.entity.User;
import com.app.backend.enums.Role;
import com.app.backend.exception.BadRequestException;
import com.app.backend.exception.UnauthorizeException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenFacade tokenFacade;

    @InjectMocks
    private AuthService authService;

    private User user;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword("encodedPassword123");
        user.setRole(Role.USER);

        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("Register: should create user and issue tokens")
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setPassword("password123");

        AuthResponse expectedResponse = new AuthResponse(1L, "alice@example.com", Role.USER, "access-token");
        when(userService.create(request)).thenReturn(user);
        when(tokenFacade.issueTokens(user, response)).thenReturn(expectedResponse);

        AuthResponse actual = authService.register(request, response);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(userService).create(request);
        verify(tokenFacade).issueTokens(user, response);
    }

    @Test
    @DisplayName("Login: should authenticate and issue tokens when credentials are valid")
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("alice@example.com");
        request.setPassword("correctPassword");

        when(userService.findUser("alice@example.com")).thenReturn(user);
        when(passwordEncoder.matches("correctPassword", "encodedPassword123")).thenReturn(true);
        AuthResponse expectedResponse = new AuthResponse(1L, "alice@example.com", Role.USER, "new-access-token");
        when(tokenFacade.issueTokens(user, response)).thenReturn(expectedResponse);

        AuthResponse actual = authService.login(request, response);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(userService).findUser("alice@example.com");
        verify(passwordEncoder).matches("correctPassword", "encodedPassword123");
        verify(tokenFacade).issueTokens(user, response);
    }

    @Test
    @DisplayName("Login: should throw BadRequestException when password does not match")
    void testLogin_InvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("alice@example.com");
        request.setPassword("wrongPassword");

        when(userService.findUser("alice@example.com")).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", "encodedPassword123")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request, response))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Invalid password");

        verify(tokenFacade, never()).issueTokens(any(), any());
    }

    @Test
    @DisplayName("Refresh: should issue new tokens when refresh token is valid and matches cache")
    void testRefresh_Success() {
        String refreshToken = "valid-refresh-token";
        when(tokenFacade.extractSubjectFromToken(refreshToken)).thenReturn("alice@example.com");
        when(userService.findUser("alice@example.com")).thenReturn(user);
        when(tokenService.get(1L)).thenReturn(refreshToken);

        AuthResponse expectedResponse = new AuthResponse(1L, "alice@example.com", Role.USER, "refreshed-access-token");
        when(tokenFacade.issueTokens(user, response)).thenReturn(expectedResponse);

        AuthResponse actual = authService.refresh(refreshToken, response);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(tokenFacade).issueTokens(user, response);
    }

    @Test
    @DisplayName("Refresh: should throw UnauthorizeException when cached token is null or mismatched")
    void testRefresh_TokenMismatch() {
        String refreshToken = "given-token";
        when(tokenFacade.extractSubjectFromToken(refreshToken)).thenReturn("alice@example.com");
        when(userService.findUser("alice@example.com")).thenReturn(user);
        when(tokenService.get(1L)).thenReturn("different-cached-token");

        assertThatThrownBy(() -> authService.refresh(refreshToken, response))
                .isInstanceOf(UnauthorizeException.class)
                .hasMessage("Invalid refreshToken");

        when(tokenService.get(1L)).thenReturn(null);

        assertThatThrownBy(() -> authService.refresh(refreshToken, response))
                .isInstanceOf(UnauthorizeException.class)
                .hasMessage("Invalid refreshToken");
    }

    @Test
    @DisplayName("Logout: should validate token and delete from cache & cookies")
    void testLogout_Success() {
        String refreshToken = "valid-token";
        when(tokenFacade.extractSubjectFromToken(refreshToken)).thenReturn("alice@example.com");
        when(userService.findUser("alice@example.com")).thenReturn(user);
        when(tokenService.get(1L)).thenReturn(refreshToken);

        authService.logout(refreshToken, response);

        verify(tokenFacade).validateToken(refreshToken);
        verify(tokenFacade).deleteTokenAndClearCookie(user, response);
    }

    @Test
    @DisplayName("Logout: should throw UnauthorizeException if token does not match cache")
    void testLogout_TokenMismatch() {
        String refreshToken = "mismatched-token";
        when(tokenFacade.extractSubjectFromToken(refreshToken)).thenReturn("alice@example.com");
        when(userService.findUser("alice@example.com")).thenReturn(user);
        when(tokenService.get(1L)).thenReturn("cached-token");

        assertThatThrownBy(() -> authService.logout(refreshToken, response))
                .isInstanceOf(UnauthorizeException.class)
                .hasMessage("Invalid or expired token");

        verify(tokenFacade, never()).deleteTokenAndClearCookie(any(), any());
    }

    @Test
    @DisplayName("ForgotPassword: should find user and call resetToken")
    void testForgotPassword() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("alice@example.com");

        when(userService.findUser("alice@example.com")).thenReturn(user);

        authService.forgotPassword(request);

        verify(userService).findUser("alice@example.com");
        verify(tokenFacade).resetToken(user);
    }

    @Test
    @DisplayName("ResetPassword: should validate token, update encoded password, and save user")
    void testResetPassword() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("reset-token-abc");
        request.setPassword("brandNewPassword123");

        when(tokenFacade.extractSubjectFromToken("reset-token-abc")).thenReturn("alice@example.com");
        when(userService.findUser("alice@example.com")).thenReturn(user);
        when(passwordEncoder.encode("brandNewPassword123")).thenReturn("newEncoded123");

        authService.resetPassword(request);

        verify(tokenFacade).validateToken("reset-token-abc");
        verify(userService).findUser("alice@example.com");
        verify(passwordEncoder).encode("brandNewPassword123");
        verify(userService).saveUser(user);
        assertThat(user.getPassword()).isEqualTo("newEncoded123");
    }
}
