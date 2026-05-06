package com.fanatic.dto.payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private UUID id;
    private UUID userId;
    private UUID contentId;
    private String contentTitle;
    private BigDecimal amount;
    private String currency;
    private String paymentStatus;
    private String paymentProvider;
    private ZonedDateTime createdAt;
}