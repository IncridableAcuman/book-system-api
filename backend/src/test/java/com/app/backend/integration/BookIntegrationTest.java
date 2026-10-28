package com.app.backend.integration;

import com.app.backend.dto.BookRequest;
import com.app.backend.entity.Book;
import com.app.backend.entity.User;
import com.app.backend.enums.Genres;
import com.app.backend.enums.Languages;
import com.app.backend.enums.Role;
import com.app.backend.repository.BookRepository;
import com.app.backend.repository.RefreshTokenRepository;
import com.app.backend.repository.UserRepository;
import com.app.backend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private JavaMailSender javaMailSender;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User savedUser;
    private String jwtToken;
    private Book initialBook;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        savedUser = new User();
        savedUser.setUsername("booklover");
        savedUser.setEmail("booklover@example.com");
        savedUser.setPassword("password123");
        savedUser.setRole(Role.USER);
        savedUser = userRepository.save(savedUser);

        jwtToken = jwtUtil.getAccessToken(savedUser);

        // Seed an initial book for the user
        initialBook = new Book();
        initialBook.setTitle("Domain-Driven Design");
        initialBook.setContent("Tackling complexity in the heart of software");
        initialBook.setAuthor("Eric Evans");
        initialBook.setPublisher("Addison-Wesley");
        initialBook.setPublishedYear(2003);
        initialBook.setPrice(55.00);
        initialBook.setGenres(Genres.SCIENCE);
        initialBook.setLanguages(Languages.EN);
        initialBook.setUser(savedUser);
        initialBook.setCreatedAt(LocalDateTime.now());
        initialBook.setUpdatedAt(LocalDateTime.now());
        initialBook = bookRepository.save(initialBook);
    }

    @Test
    @DisplayName("Integration: Unauthenticated access to /api/book should be forbidden/unauthorized")
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/book"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Integration: Book CRUD operations with JWT authentication")
    void testBookCrudLifecycle() throws Exception {
        // 1. Create a new book
        BookRequest createRequest = new BookRequest();
        createRequest.setTitle("Effective Java");
        createRequest.setContent("Best practices for the Java platform");
        createRequest.setAuthor("Joshua Bloch");
        createRequest.setPublisher("Addison-Wesley");
        createRequest.setPublishedYear(2018);
        createRequest.setPrice(45.00);
        createRequest.setGenres(Genres.SCIENCE);
        createRequest.setLanguages(Languages.EN);

        mockMvc.perform(post("/api/book")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.author").value("Joshua Bloch"))
                .andExpect(jsonPath("$.userId").value(savedUser.getId()));

        assertThat(bookRepository.count()).isEqualTo(2);

        // 2. Get list of books for current user
        mockMvc.perform(get("/api/book")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // 3. Get single book by id
        mockMvc.perform(get("/api/book/list/" + initialBook.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(initialBook.getId()))
                .andExpect(jsonPath("$.title").value("Domain-Driven Design"));

        // 4. Filter books by author
        mockMvc.perform(get("/api/book/list/authors/Eric Evans")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("Eric Evans"));

        // 5. Filter books by language
        mockMvc.perform(get("/api/book/list/languages/EN")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // 6. Filter books by genre
        mockMvc.perform(get("/api/book/list/genres/SCIENCE")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // 7. Edit the book
        BookRequest editRequest = new BookRequest();
        editRequest.setTitle("Domain-Driven Design - 2nd Edition");
        editRequest.setContent("Updated content");
        editRequest.setAuthor("Eric Evans");
        editRequest.setPublisher("Addison-Wesley");
        editRequest.setPublishedYear(2024);
        editRequest.setPrice(65.00);
        editRequest.setGenres(Genres.SCIENCE);
        editRequest.setLanguages(Languages.EN);

        mockMvc.perform(patch("/api/book/edit/" + initialBook.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Domain-Driven Design - 2nd Edition"))
                .andExpect(jsonPath("$.publishedYear").value(2024))
                .andExpect(jsonPath("$.price").value(55.00));

        // 8. Delete book
        mockMvc.perform(delete("/api/book/" + initialBook.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted successfully"));

        assertThat(bookRepository.findById(initialBook.getId())).isEmpty();
    }

    @Test
    @DisplayName("Integration: New user with no books gets empty list successfully")
    void testUserWithNoBooksReturnsEmptyList() throws Exception {
        // Create user with no books
        User userWithoutBooks = new User();
        userWithoutBooks.setUsername("nobooksuser");
        userWithoutBooks.setEmail("nobooks@example.com");
        userWithoutBooks.setPassword("password");
        userWithoutBooks.setRole(Role.USER);
        userWithoutBooks = userRepository.save(userWithoutBooks);

        String tokenWithoutBooks = jwtUtil.getAccessToken(userWithoutBooks);

        mockMvc.perform(get("/api/book")
                        .header("Authorization", "Bearer " + tokenWithoutBooks))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
