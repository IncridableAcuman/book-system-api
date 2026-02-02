package com.app.backend.dto;

import com.app.backend.entity.User;
import com.app.backend.enums.Role;

public record AuthResponse(
        Long id,
        String email,
        Role role,
        String accessToken
) {
    public static AuthResponse from(User user,String accessToken){
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                accessToken
        );
    }
}
