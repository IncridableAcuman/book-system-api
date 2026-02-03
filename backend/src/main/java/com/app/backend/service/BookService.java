package com.app.backend.service;

import com.app.backend.dto.BookRequest;
import com.app.backend.dto.BookResponse;
import com.app.backend.entity.Book;
import com.app.backend.entity.User;
import com.app.backend.enums.Genres;
import com.app.backend.enums.Languages;
import com.app.backend.exception.NotFoundException;
import com.app.backend.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public void findUser(User user){
        bookRepository.findByUser(user).orElseThrow(()->new NotFoundException("User not found!"));
    }

    @Transactional
    public BookResponse create(User user, BookRequest request){
        findUser(user);
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setContent(request.getContent());
        book.setAuthor(request.getAuthor());
        book.setPublishedYear(request.getPublishedYear());
        book.setPublisher(request.getPublisher());
        book.setPrice(request.getPrice());
        book.setUser(user);
        book.setGenres(request.getGenres());
        book.setLanguages(request.getLanguages());
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        book = bookRepository.save(book);
        return BookResponse.from(book);
    }

    public List<BookResponse> getList(User user){
        findUser(user);
        List<Book> books = bookRepository.findBuUser(user);
        return books.stream().map(BookResponse::from).toList();
    }

    public BookResponse getBook(Long id,User user){
        findUser(user);
        Book book = bookRepository.findById(id).orElseThrow(()->new NotFoundException("Book not found!"));
        return BookResponse.from(book);
    }

    public List<BookResponse> getBooksByAuthor(User user,String author){
        findUser(user);
        List<Book> books = bookRepository.findByAuthor(author);
        return books.stream().map(BookResponse::from).toList();
    }

    public List<BookResponse> getBooksByLanguages(User user,Languages languages){
        findUser(user);
        List<Book> books = bookRepository.findByLanguages(languages);
        return books.stream().map(BookResponse::from).toList();
    }
    public List<BookResponse> getBooksByGenres(User user, Genres genres){
        findUser(user);
        List<Book> books = bookRepository.findByGenres(genres);
        return books.stream().map(BookResponse::from).toList();
    }
    @Transactional
    public void deleteBook(Long id){
        Book book = bookRepository.findById(id).orElseThrow(()->new NotFoundException("Book not found"));
        bookRepository.delete(book);
    }
    @Transactional
    public BookResponse editBook(Long id,BookRequest request){
        Book book = bookRepository.findById(id).orElseThrow(()->new NotFoundException("Book not found"));
        book.setTitle(request.getTitle());
        book.setContent(request.getContent());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setPublishedYear(request.getPublishedYear());
        book.setGenres(request.getGenres());
        book.setLanguages(request.getLanguages());
        book.setUpdatedAt(LocalDateTime.now());
        book = bookRepository.save(book);
        return BookResponse.from(book);
    }
}
