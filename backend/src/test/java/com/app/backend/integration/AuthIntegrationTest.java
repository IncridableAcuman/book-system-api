package com.app.backend.integration;

import com.app.backend.dto.*;
import com.app.backend.entity.User;
import com.app.backend.repository.RefreshTokenRepository;
import com.app.backend.repository.UserRepository;
import com.app.backend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration: Register -> Login -> Refresh -> Logout full flow")
    void testAuthFullLifecycle() throws Exception {
        // 1. Register new user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("integratetest");
        registerRequest.setEmail("integration@example.com");
        registerRequest.setPassword("strongPassword123");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("integration@example.com"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(cookie().exists("refreshToken"))
                .andReturn();

        // Verify user in repository
        User savedUser = userRepository.findByEmail("integration@example.com").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("integratetest");
        assertThat(passwordEncoder.matches("strongPassword123", savedUser.getPassword())).isTrue();

        // Verify refresh token stored in DB
        assertThat(refreshTokenRepository.findByUserId(savedUser.getId())).isPresent();

        // 2. Duplicate registration should fail
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User already exist."));

        // 3. Login with wrong password should fail
        LoginRequest badLogin = new LoginRequest();
        badLogin.setEmail("integration@example.com");
        badLogin.setPassword("wrongPassword123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid password"));

        // 4. Login with correct credentials
        LoginRequest correctLogin = new LoginRequest();
        correctLogin.setEmail("integration@example.com");
        correctLogin.setPassword("strongPassword123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(cookie().exists("refreshToken"))
                .andReturn();

        Cookie refreshCookie = loginResult.getResponse().getCookie("refreshToken");
        assertThat(refreshCookie).isNotNull();

        // 5. Refresh token using cookie
        MvcResult refreshResult = mockMvc.perform(get("/api/auth/refresh")
                        .cookie(refreshCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        Cookie newRefreshCookie = refreshResult.getResponse().getCookie("refreshToken");
        assertThat(newRefreshCookie).isNotNull();

        // Token in DB is updated with new refresh token
        assertThat(refreshTokenRepository.findByUserId(savedUser.getId())).isPresent();
        assertThat(refreshTokenRepository.findByUserId(savedUser.getId()).get().getToken())
                .isEqualTo(newRefreshCookie.getValue());

        // 6. Logout
        mockMvc.perform(post("/api/auth/logout")
                        .cookie(newRefreshCookie))
                .andExpect(status().isOk())
                .andExpect(content().string("User logged out"));

        // After logout, token should be removed from database
        assertThat(refreshTokenRepository.findByUserId(savedUser.getId())).isEmpty();
    }

    @Test
    @DisplayName("Integration: ForgotPassword & ResetPassword flow")
    void testForgotAndResetPasswordFlow() throws Exception {
        // Pre-create user
        User user = new User();
        user.setUsername("resetuser");
        user.setEmail("reset@example.com");
        user.setPassword(passwordEncoder.encode("oldPassword123"));
        userRepository.save(user);

        // 1. Request forgot password
        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest();
        forgotRequest.setEmail("reset@example.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Reset password link send to email"));

        verify(javaMailSender, atLeastOnce()).send(any(SimpleMailMessage.class));

        // Generate valid reset token for the user
        String resetToken = jwtUtil.generateResetToken(user);

        // 2. Perform reset password
        ResetPasswordRequest resetRequest = new ResetPasswordRequest();
        resetRequest.setToken(resetToken);
        resetRequest.setPassword("brandNewSecurePassword456");

        mockMvc.perform(put("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password updated successfully"));

        // 3. User should now be able to login with the new password
        LoginRequest loginWithNewPass = new LoginRequest();
        loginWithNewPass.setEmail("reset@example.com");
        loginWithNewPass.setPassword("brandNewSecurePassword456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginWithNewPass)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}
