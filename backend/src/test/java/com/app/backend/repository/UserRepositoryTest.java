package com.app.backend.repository;

import com.app.backend.entity.User;
import com.app.backend.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(UserRepositoryTest.TestCacheConfig.class)
class UserRepositoryTest {

    @TestConfiguration
    static class TestCacheConfig {
        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save user and find by email")
    void testFindByEmail_Success() {
        User user = new User();
        user.setUsername("bob");
        user.setEmail("bob@example.com");
        user.setPassword("secret123");
        user.setRole(Role.USER);

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("bob@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isNotNull();
        assertThat(found.get().getUsername()).isEqualTo("bob");
        assertThat(found.get().getEmail()).isEqualTo("bob@example.com");
        assertThat(found.get().getRole()).isEqualTo(Role.USER);
        assertThat(found.get().getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Should return empty optional when email does not exist")
    void testFindByEmail_NotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        assertThat(found).isEmpty();
    }
}
