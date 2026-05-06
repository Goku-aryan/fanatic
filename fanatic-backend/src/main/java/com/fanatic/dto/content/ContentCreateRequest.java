package com.fanatic.dto.content;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContentCreateRequest {
    @NotBlank
    private String title;
    
    private String description;
    
    @NotBlank
    private String contentType; // MOVIE, SERIES, BOOK
    
    private String genre;
    private String coverImageUrl;
    private LocalDate releaseDate;
    private String authorDirector;
    private Boolean isEarlyAccess;
    private BigDecimal earlyAccessPrice;
    private LocalDate earlyAccessStart;
    private LocalDate earlyAccessEnd;
}