package com.app.backend.repository;

import com.app.backend.entity.Book;
import com.app.backend.entity.User;
import com.app.backend.enums.Genres;
import com.app.backend.enums.Languages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {
    List<Book> findByAuthor(String author);
    List<Book> findByGenres(Genres genres);
    List<Book> findByLanguages(Languages languages);
    List<Book> findBuUser(User user);
    Optional<Book> findByUser(User user);
}
