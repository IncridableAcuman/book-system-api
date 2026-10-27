package com.app.backend.service;

import com.app.backend.dto.RegisterRequest;
import com.app.backend.entity.User;
import com.app.backend.enums.Role;
import com.app.backend.exception.BadRequestException;
import com.app.backend.exception.NotFoundException;
import com.app.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("plainPassword123");
    }

    @Test
    @DisplayName("Should successfully create and save a new user")
    void testCreate_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        User savedUser = userService.create(registerRequest);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(1L);
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword123");
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);

        verify(userRepository).save(any(User.class));
        verify(userRepository).flush();
    }

    @Test
    @DisplayName("Should throw BadRequestException if user email already exists")
    void testCreate_UserAlreadyExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.create(registerRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("User already exist.");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return user when email is found")
    void testFindUser_Success() {
        User user = new User();
        user.setId(10L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User found = userService.findUser("test@example.com");

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Should throw NotFoundException when email is not found")
    void testFindUser_NotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUser("notfound@example.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found.");
    }

    @Test
    @DisplayName("Should save user and flush")
    void testSaveUser() {
        User user = new User();
        user.setEmail("save@example.com");
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.saveUser(user);

        assertThat(result).isSameAs(user);
        verify(userRepository).save(user);
        verify(userRepository).flush();
    }

    @Test
    @DisplayName("Should edit user information")
    void testEdit() {
        User existingUser = new User();
        existingUser.setId(5L);
        existingUser.setUsername("oldName");
        existingUser.setEmail("test@example.com");
        existingUser.setPassword("oldPass");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("plainPassword123")).thenReturn("newEncodedPass");
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        userService.edit(registerRequest);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User updated = captor.getValue();
        assertThat(updated.getUsername()).isEqualTo("testuser");
        assertThat(updated.getPassword()).isEqualTo("newEncodedPass");
    }
}
