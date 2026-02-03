package com.app.backend.dto;

import com.app.backend.entity.Book;
import com.app.backend.enums.Genres;
import com.app.backend.enums.Languages;

import java.time.LocalDateTime;

public record BookResponse(
        Long id,
        String title,
        String content,
        String author,
        String publisher,
        int publishedYear,
        Genres genres,
        double price,
        Languages languages,
        Long userId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static BookResponse from(Book book){
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getContent(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishedYear(),
                book.getGenres(),
                book.getPrice(),
                book.getLanguages(),
                book.getUser().getId(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}
