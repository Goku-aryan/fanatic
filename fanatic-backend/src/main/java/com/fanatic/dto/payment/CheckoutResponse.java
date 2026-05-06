package com.fanatic.dto.payment;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponse {
    private String sessionId;
    private String sessionUrl;
    private String publishableKey;
}