package com.app.backend.controller;

import com.app.backend.dto.*;
import com.app.backend.enums.Role;
import com.app.backend.exception.BadRequestException;
import com.app.backend.exception.GlobalException;
import com.app.backend.exception.UnauthorizeException;
import com.app.backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalException())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /api/auth/register - success")
    void testRegister_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("charlie");
        request.setEmail("charlie@example.com");
        request.setPassword("password123");

        AuthResponse authResponse = new AuthResponse(1L, "charlie@example.com", Role.USER, "jwt-token");
        when(authService.register(any(), any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("charlie@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }

    @Test
    @DisplayName("POST /api/auth/register - validation failure (invalid email / short password)")
    void testRegister_ValidationFailure() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(""); // blank
        request.setEmail("not-an-email");
        request.setPassword("123"); // < 8 characters

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - success")
    void testLogin_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("charlie@example.com");
        request.setPassword("password123");

        AuthResponse authResponse = new AuthResponse(1L, "charlie@example.com", Role.USER, "login-jwt-token");
        when(authService.login(any(), any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("login-jwt-token"));
    }

    @Test
    @DisplayName("POST /api/auth/login - invalid password returns bad request")
    void testLogin_InvalidPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("charlie@example.com");
        request.setPassword("wrongpassword");

        when(authService.login(any(), any())).thenThrow(new BadRequestException("Invalid password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid password"));
    }

    @Test
    @DisplayName("GET /api/auth/refresh - success")
    void testRefresh_Success() throws Exception {
        AuthResponse authResponse = new AuthResponse(1L, "charlie@example.com", Role.USER, "refreshed-token");
        when(authService.refresh(eq("my-refresh-token"), any())).thenReturn(authResponse);

        mockMvc.perform(get("/api/auth/refresh")
                        .cookie(new Cookie("refreshToken", "my-refresh-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("refreshed-token"));
    }

    @Test
    @DisplayName("GET /api/auth/refresh - unauthorized on invalid token")
    void testRefresh_Unauthorized() throws Exception {
        when(authService.refresh(eq("invalid-token"), any())).thenThrow(new UnauthorizeException("Invalid refreshToken"));

        mockMvc.perform(get("/api/auth/refresh")
                        .cookie(new Cookie("refreshToken", "invalid-token")))
                .andExpect(status().isBadRequest()) // Handled by GlobalException returning BAD_REQUEST status
                .andExpect(jsonPath("$.message").value("Invalid refreshToken"));
    }

    @Test
    @DisplayName("POST /api/auth/logout - success")
    void testLogout_Success() throws Exception {
        doNothing().when(authService).logout(eq("logout-refresh-token"), any());

        mockMvc.perform(post("/api/auth/logout")
                        .cookie(new Cookie("refreshToken", "logout-refresh-token")))
                .andExpect(status().isOk())
                .andExpect(content().string("User logged out"));
    }

    @Test
    @DisplayName("POST /api/auth/forgot-password - success")
    void testForgotPassword_Success() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("charlie@example.com");

        doNothing().when(authService).forgotPassword(any());

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Reset password link send to email"));
    }

    @Test
    @DisplayName("PUT /api/auth/reset-password - success")
    void testResetPassword_Success() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("reset-token-xyz");
        request.setPassword("newValidPassword123");

        doNothing().when(authService).resetPassword(any());

        mockMvc.perform(put("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password updated successfully"));
    }
}
