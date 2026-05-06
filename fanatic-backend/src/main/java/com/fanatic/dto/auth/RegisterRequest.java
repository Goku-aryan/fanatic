package com.fanatic.dto.auth;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Email
    private String email;
    
    @NotBlank @Size(min = 3, max = 30)
    private String username;
    
    @NotBlank @Size(min = 6, max = 100)
    private String password;
    
    @Size(max = 200)
    private String fullName;
}