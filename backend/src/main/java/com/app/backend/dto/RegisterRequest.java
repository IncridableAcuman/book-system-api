package com.app.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username must be required!")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters long!")
    private String username;

    @NotBlank(message = "Email must be required!")
    @Email
    private String email;

    @NotBlank(message = "Password must be required!")
    @Size(min = 8, message = "Password must be greater 8 characters long!")
    private String password;
}
