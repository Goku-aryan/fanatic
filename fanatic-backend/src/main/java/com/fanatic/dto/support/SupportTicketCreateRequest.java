package com.fanatic.dto.support;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupportTicketCreateRequest {
    @NotBlank
    private String subject;
    
    @NotBlank
    private String message;
}