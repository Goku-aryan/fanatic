package com.fanatic.dto.support;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupportTicketDTO {
    private UUID id;
    private UUID userId;
    private String username;
    private String subject;
    private String message;
    private String status;
    private String adminResponse;
    private String respondedByUsername;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}