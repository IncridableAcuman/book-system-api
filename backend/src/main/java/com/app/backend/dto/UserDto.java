package com.app.backend.dto;

import com.app.backend.enums.Role;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Role role;
}
