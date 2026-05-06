package com.fanatic.dto.review;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ReviewCreateRequest {
    @NotNull
    private UUID contentId;
    
    private String title;
    
    @NotBlank
    private String body;
    
    @NotNull
    @DecimalMin("0.0") @DecimalMax("5.0")
    private BigDecimal rating;
    
    private Boolean isSpoiler;
}