package com.app.backend.service;

import com.app.backend.dto.BookRequest;
import com.app.backend.dto.BookResponse;
import com.app.backend.entity.Book;
import com.app.backend.entity.User;
import com.app.backend.enums.Genres;
import com.app.backend.enums.Languages;
import com.app.backend.enums.Role;
import com.app.backend.exception.NotFoundException;
import com.app.backend.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private User user;
    private Book sampleBook;
    private BookRequest sampleRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(10L);
        user.setUsername("bookuser");
        user.setEmail("bookuser@example.com");
        user.setRole(Role.USER);

        sampleBook = new Book();
        sampleBook.setId(1L);
        sampleBook.setTitle("Spring Boot in Action");
        sampleBook.setContent("Comprehensive guide to Spring Boot");
        sampleBook.setAuthor("Craig Walls");
        sampleBook.setPublisher("Manning");
        sampleBook.setPublishedYear(2016);
        sampleBook.setPrice(39.99);
        sampleBook.setGenres(Genres.SCIENCE);
        sampleBook.setLanguages(Languages.EN);
        sampleBook.setUser(user);
        sampleBook.setCreatedAt(LocalDateTime.now());
        sampleBook.setUpdatedAt(LocalDateTime.now());

        sampleRequest = new BookRequest();
        sampleRequest.setTitle("Spring Boot in Action");
        sampleRequest.setContent("Comprehensive guide to Spring Boot");
        sampleRequest.setAuthor("Craig Walls");
        sampleRequest.setPublisher("Manning");
        sampleRequest.setPublishedYear(2016);
        sampleRequest.setPrice(39.99);
        sampleRequest.setGenres(Genres.SCIENCE);
        sampleRequest.setLanguages(Languages.EN);
    }

    @Test
    @DisplayName("findUser should pass when book exists for user")
    void testFindUser_Success() {
        when(bookRepository.findFirstByUser(user)).thenReturn(Optional.of(sampleBook));

        bookService.findUser(user);

        verify(bookRepository).findFirstByUser(user);
    }

    @Test
    @DisplayName("findUser should throw NotFoundException when no book exists for user")
    void testFindUser_NotFound() {
        when(bookRepository.findFirstByUser(user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findUser(user))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found!");
    }

    @Test
    @DisplayName("Create: should successfully create and return BookResponse")
    void testCreate_Success() {
        when(bookRepository.findFirstByUser(user)).thenReturn(Optional.of(sampleBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> {
            Book b = inv.getArgument(0);
            b.setId(2L);
            return b;
        });

        BookResponse response = bookService.create(user, sampleRequest);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.title()).isEqualTo("Spring Boot in Action");
        assertThat(response.author()).isEqualTo("Craig Walls");
        assertThat(response.price()).isEqualTo(39.99);
        assertThat(response.userId()).isEqualTo(10L);

        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Create: should throw NotFoundException when findUser fails")
    void testCreate_UserNotFound() {
        when(bookRepository.findFirstByUser(user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.create(user, sampleRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found!");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("getList: should return all books for user")
    void testGetList_Success() {
        when(bookRepository.findFirstByUser(user)).thenReturn(Optional.of(sampleBook));
        when(bookRepository.findByUser(user)).thenReturn(List.of(sampleBook));

        List<BookResponse> list = bookService.getList(user);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).title()).isEqualTo("Spring Boot in Action");
        verify(bookRepository).findByUser(user);
    }

    @Test
    @DisplayName("getBook: should return book by id")
    void testGetBook_Success() {
        when(bookRepository.findFirstByUser(user)).thenReturn(Optional.of(sampleBook));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        BookResponse response = bookService.getBook(1L, user);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Spring Boot in Action");
    }

    @Test
    @DisplayName("getBook: should throw NotFoundException when book id does not exist")
    void testGetBook_NotFound() {
        when(bookRepository.findFirstByUser(user)).thenReturn(Optional.of(sampleBook));
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBook(99L, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Book not found!");
    }

    @Test
    @DisplayName("getBooksByAuthor: should return books filtered by author")
    void testGetBooksByAuthor() {
        when(bookRepository.findFirstByUser(user)).thenReturn(Optional.of(sampleBook));
        when(bookRepository.findByAuthor("Craig Walls")).thenReturn(List.of(sampleBook));

        List<BookResponse> result = bookService.getBooksByAuthor(user, "Craig Walls");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).author()).isEqualTo("Craig Walls");
    }

    @Test
    @DisplayName("getBooksByLanguages: should return books filtered by language")
    void testGetBooksByLanguages() {
        when(bookRepository.findFirstByUser(user)).thenReturn(Optional.of(sampleBook));
        when(bookRepository.findByLanguages(Languages.EN)).thenReturn(List.of(sampleBook));

        List<BookResponse> result = bookService.getBooksByLanguages(user, Languages.EN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).languages()).isEqualTo(Languages.EN);
    }

    @Test
    @DisplayName("getBooksByGenres: should return books filtered by genre")
    void testGetBooksByGenres() {
        when(bookRepository.findFirstByUser(user)).thenReturn(Optional.of(sampleBook));
        when(bookRepository.findByGenres(Genres.SCIENCE)).thenReturn(List.of(sampleBook));

        List<BookResponse> result = bookService.getBooksByGenres(user, Genres.SCIENCE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).genres()).isEqualTo(Genres.SCIENCE);
    }

    @Test
    @DisplayName("deleteBook: should delete existing book")
    void testDeleteBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        bookService.deleteBook(1L);

        verify(bookRepository).delete(sampleBook);
    }

    @Test
    @DisplayName("deleteBook: should throw NotFoundException when book not found")
    void testDeleteBook_NotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Book not found");

        verify(bookRepository, never()).delete(any());
    }

    @Test
    @DisplayName("editBook: should update book fields and return response")
    void testEditBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        BookRequest updateRequest = new BookRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setContent("Updated Content");
        updateRequest.setAuthor("Updated Author");
        updateRequest.setPublisher("Updated Publisher");
        updateRequest.setPublishedYear(2022);
        updateRequest.setPrice(49.99);
        updateRequest.setGenres(Genres.FANTASY);
        updateRequest.setLanguages(Languages.RU);

        BookResponse updated = bookService.editBook(1L, updateRequest);

        assertThat(updated).isNotNull();
        assertThat(updated.title()).isEqualTo("Updated Title");
        assertThat(updated.author()).isEqualTo("Updated Author");
        assertThat(updated.genres()).isEqualTo(Genres.FANTASY);
        assertThat(updated.languages()).isEqualTo(Languages.RU);
        verify(bookRepository).save(sampleBook);
    }

    @Test
    @DisplayName("editBook: should throw NotFoundException when book not found")
    void testEditBook_NotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.editBook(999L, sampleRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Book not found");
    }
}
