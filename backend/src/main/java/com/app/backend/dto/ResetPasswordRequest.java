package com.app.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Token must be required!")
    private String token;

    @NotBlank(message = "Password must be required!")
    @Size(min = 8, message = "Password must be greater 8 characters long!")
    private String password;
}
