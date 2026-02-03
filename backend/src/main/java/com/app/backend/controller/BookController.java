package com.app.backend.controller;

import com.app.backend.dto.BookRequest;
import com.app.backend.dto.BookResponse;
import com.app.backend.entity.User;
import com.app.backend.enums.Genres;
import com.app.backend.enums.Languages;
import com.app.backend.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookResponse> create(@AuthenticationPrincipal User user, @Valid @RequestBody BookRequest request){
        return ResponseEntity.ok(bookService.create(user,request));
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getList(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(bookService.getList(user));
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long id,@AuthenticationPrincipal User user){
        return ResponseEntity.ok(bookService.getBook(id,user));
    }

    @GetMapping("/list/authors/{author}")
    public ResponseEntity<List<BookResponse>> getBooksByAuthor(@AuthenticationPrincipal User user,@PathVariable String author){
        return ResponseEntity.ok(bookService.getBooksByAuthor(user,author));
    }

    @GetMapping("/list/languages/{languages}")
    public ResponseEntity<List<BookResponse>> getBooksByLanguages(@AuthenticationPrincipal User user,@PathVariable Languages languages){
        return ResponseEntity.ok(bookService.getBooksByLanguages(user,languages));
    }

    @GetMapping("/list/genres/{genres}")
    public ResponseEntity<List<BookResponse>> getBookByGenres(@AuthenticationPrincipal User user, @PathVariable Genres genres){
        return ResponseEntity.ok(bookService.getBooksByGenres(user,genres));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully");
    }
    @PatchMapping("/edit/{id}")
    public ResponseEntity<BookResponse> editBook(@PathVariable Long id, @Valid @RequestBody BookRequest request){
        return ResponseEntity.ok(bookService.editBook(id,request));
    }
}
