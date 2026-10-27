package com.app.backend.repository;

import com.app.backend.entity.Book;
import com.app.backend.entity.User;
import com.app.backend.enums.Genres;
import com.app.backend.enums.Languages;
import com.app.backend.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(BookRepositoryTest.TestCacheConfig.class)
class BookRepositoryTest {

    @TestConfiguration
    static class TestCacheConfig {
        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }
    }

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("bookauthor");
        user.setEmail("author@example.com");
        user.setPassword("password123");
        user.setRole(Role.USER);
        savedUser = userRepository.save(user);

        book1 = new Book();
        book1.setTitle("Clean Code");
        book1.setContent("A handbook of agile software craftsmanship");
        book1.setAuthor("Robert C. Martin");
        book1.setPublisher("Prentice Hall");
        book1.setPublishedYear(2008);
        book1.setPrice(45.99);
        book1.setGenres(Genres.SCIENCE);
        book1.setLanguages(Languages.EN);
        book1.setUser(savedUser);
        book1.setCreatedAt(LocalDateTime.now());
        book1.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book1);

        book2 = new Book();
        book2.setTitle("O'tkan Kunlar");
        book2.setContent("Tarixiy roman");
        book2.setAuthor("Abdulla Qodiriy");
        book2.setPublisher("Sharq");
        book2.setPublishedYear(1925);
        book2.setPrice(25.50);
        book2.setGenres(Genres.HISTORICAL);
        book2.setLanguages(Languages.UZB);
        book2.setUser(savedUser);
        book2.setCreatedAt(LocalDateTime.now());
        book2.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book2);
    }

    @Test
    @DisplayName("Should find books by author")
    void testFindByAuthor() {
        List<Book> books = bookRepository.findByAuthor("Robert C. Martin");
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Clean Code");

        List<Book> empty = bookRepository.findByAuthor("Unknown Author");
        assertThat(empty).isEmpty();
    }

    @Test
    @DisplayName("Should find books by genre")
    void testFindByGenres() {
        List<Book> scienceBooks = bookRepository.findByGenres(Genres.SCIENCE);
        assertThat(scienceBooks).hasSize(1);
        assertThat(scienceBooks.get(0).getTitle()).isEqualTo("Clean Code");

        List<Book> historicalBooks = bookRepository.findByGenres(Genres.HISTORICAL);
        assertThat(historicalBooks).hasSize(1);
        assertThat(historicalBooks.get(0).getTitle()).isEqualTo("O'tkan Kunlar");
    }

    @Test
    @DisplayName("Should find books by language")
    void testFindByLanguages() {
        List<Book> uzbBooks = bookRepository.findByLanguages(Languages.UZB);
        assertThat(uzbBooks).hasSize(1);
        assertThat(uzbBooks.get(0).getTitle()).isEqualTo("O'tkan Kunlar");

        List<Book> engBooks = bookRepository.findByLanguages(Languages.EN);
        assertThat(engBooks).hasSize(1);
        assertThat(engBooks.get(0).getTitle()).isEqualTo("Clean Code");
    }

    @Test
    @DisplayName("Should find all books by user")
    void testFindByUser() {
        List<Book> userBooks = bookRepository.findByUser(savedUser);
        assertThat(userBooks).hasSize(2);
    }

    @Test
    @DisplayName("Should find first book by user")
    void testFindFirstByUser() {
        Optional<Book> firstBook = bookRepository.findFirstByUser(savedUser);
        assertThat(firstBook).isPresent();

        User anotherUser = new User();
        anotherUser.setUsername("nouser");
        anotherUser.setEmail("nouser@example.com");
        anotherUser.setPassword("pass");
        anotherUser.setRole(Role.USER);
        anotherUser = userRepository.save(anotherUser);

        Optional<Book> empty = bookRepository.findFirstByUser(anotherUser);
        assertThat(empty).isEmpty();
    }
}
