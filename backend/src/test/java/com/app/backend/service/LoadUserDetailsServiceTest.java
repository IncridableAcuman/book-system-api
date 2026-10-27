package com.app.backend.service;

import com.app.backend.entity.User;
import com.app.backend.enums.Role;
import com.app.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoadUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoadUserDetailsService loadUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("user@example.com");
        user.setPassword("secretPassword");
        user.setRole(Role.USER);
    }

    @Test
    @DisplayName("Should successfully load user by email")
    void testLoadUserByUsername_Success() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = loadUserDetailsService.loadUserByUsername("user@example.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
        verify(userRepository).findByEmail("user@example.com");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found")
    void testLoadUserByUsername_NotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loadUserDetailsService.loadUserByUsername("notfound@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }
}
