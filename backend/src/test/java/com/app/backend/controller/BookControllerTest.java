package com.app.backend.controller;

import com.app.backend.dto.BookRequest;
import com.app.backend.dto.BookResponse;
import com.app.backend.entity.User;
import com.app.backend.enums.Genres;
import com.app.backend.enums.Languages;
import com.app.backend.enums.Role;
import com.app.backend.exception.GlobalException;
import com.app.backend.exception.NotFoundException;
import com.app.backend.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    private BookResponse sampleResponse;
    private BookRequest sampleRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.USER);

        HandlerMethodArgumentResolver userResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return testUser;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setCustomArgumentResolvers(userResolver)
                .setControllerAdvice(new GlobalException())
                .build();

        objectMapper = new ObjectMapper();

        sampleResponse = new BookResponse(
                10L,
                "Spring Microservices",
                "Great architecture guide",
                "John Carnell",
                "Manning",
                2021,
                Genres.SCIENCE,
                42.0,
                Languages.EN,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        sampleRequest = new BookRequest();
        sampleRequest.setTitle("Spring Microservices");
        sampleRequest.setContent("Great architecture guide");
        sampleRequest.setAuthor("John Carnell");
        sampleRequest.setPublisher("Manning");
        sampleRequest.setPublishedYear(2021);
        sampleRequest.setPrice(42.0);
        sampleRequest.setGenres(Genres.SCIENCE);
        sampleRequest.setLanguages(Languages.EN);
    }

    @Test
    @DisplayName("POST /api/book - success")
    void testCreateBook_Success() throws Exception {
        when(bookService.create(any(), any())).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Spring Microservices"))
                .andExpect(jsonPath("$.author").value("John Carnell"));
    }

    @Test
    @DisplayName("POST /api/book - validation failure (blank title)")
    void testCreateBook_ValidationFailure() throws Exception {
        sampleRequest.setTitle(""); // blank

        mockMvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/book - get all books for current user")
    void testGetList() throws Exception {
        when(bookService.getList(any())).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].title").value("Spring Microservices"));
    }

    @Test
    @DisplayName("GET /api/book/list/{id} - get single book")
    void testGetBook_Success() throws Exception {
        when(bookService.getBook(eq(10L), any())).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/book/list/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Spring Microservices"));
    }

    @Test
    @DisplayName("GET /api/book/list/{id} - not found")
    void testGetBook_NotFound() throws Exception {
        when(bookService.getBook(eq(999L), any())).thenThrow(new NotFoundException("Book not found!"));

        mockMvc.perform(get("/api/book/list/999"))
                .andExpect(status().isBadRequest()) // GlobalException returns BAD_REQUEST status code
                .andExpect(jsonPath("$.message").value("Book not found!"));
    }

    @Test
    @DisplayName("GET /api/book/list/authors/{author} - filter by author")
    void testGetBooksByAuthor() throws Exception {
        when(bookService.getBooksByAuthor(any(), eq("John Carnell"))).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/book/list/authors/John Carnell"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("John Carnell"));
    }

    @Test
    @DisplayName("GET /api/book/list/languages/{languages} - filter by language")
    void testGetBooksByLanguages() throws Exception {
        when(bookService.getBooksByLanguages(any(), eq(Languages.EN))).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/book/list/languages/EN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].languages").value("EN"));
    }

    @Test
    @DisplayName("GET /api/book/list/genres/{genres} - filter by genre")
    void testGetBooksByGenres() throws Exception {
        when(bookService.getBooksByGenres(any(), eq(Genres.SCIENCE))).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/book/list/genres/SCIENCE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].genres").value("SCIENCE"));
    }

    @Test
    @DisplayName("DELETE /api/book/{id} - success")
    void testDeleteBook_Success() throws Exception {
        doNothing().when(bookService).deleteBook(10L);

        mockMvc.perform(delete("/api/book/10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted successfully"));
    }

    @Test
    @DisplayName("PATCH /api/book/edit/{id} - success")
    void testEditBook_Success() throws Exception {
        when(bookService.editBook(eq(10L), any())).thenReturn(sampleResponse);

        mockMvc.perform(patch("/api/book/edit/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Spring Microservices"));
    }
}
