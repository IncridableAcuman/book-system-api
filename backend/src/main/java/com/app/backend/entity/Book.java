package com.app.backend.entity;

import com.app.backend.enums.Genres;
import com.app.backend.enums.Languages;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "books")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String author;

    private String publisher;

    private int publishedYear;

    @Enumerated(EnumType.STRING)
    private Genres genres;

    private double price;

    @Enumerated(EnumType.STRING)
    private Languages languages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
