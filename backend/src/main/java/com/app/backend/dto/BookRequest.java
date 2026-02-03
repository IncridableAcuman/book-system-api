package com.app.backend.dto;

import com.app.backend.enums.Genres;
import com.app.backend.enums.Languages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookRequest {
    @NotBlank(message = "Title must be required!")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters long!")
    private String title;

    @NotBlank(message = "Content must be required!")
    @Size(min = 5, max = 150, message = "Content must be between 5 and 250 characters long!")
    private String content;

    @NotBlank(message = "Author must be required!")
    @Size(min = 3, max = 50, message = "Author must be between 3 and 50 characters long!")
    private String author;

    @NotBlank(message = "Publisher must be required!")
    @Size(min = 3, max = 150, message = "Publisher must be between 3 and 150 characters long!")
    private String publisher;

    @NotNull(message = "Published year must be required!")
    private int publishedYear;

    @NotNull(message = "Price must be required!")
    private double price;

    private Genres genres = Genres.SCIENCE;

    private Languages languages = Languages.UZB;
}
