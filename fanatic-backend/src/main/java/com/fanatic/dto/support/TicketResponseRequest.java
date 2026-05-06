package com.fanatic.dto.support;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TicketResponseRequest {
    @NotBlank
    private String response;
    
    private String status; // IN_PROGRESS, RESOLVED, CLOSED
}