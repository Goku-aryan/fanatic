package com.fanatic.controller;

import com.fanatic.dto.common.ApiResponse;
import com.fanatic.dto.common.PagedResponse;
import com.fanatic.dto.support.SupportTicketCreateRequest;
import com.fanatic.dto.support.SupportTicketDTO;
import com.fanatic.service.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
@Tag(name = "Help & Support", description = "Support Ticket APIs")
public class SupportController {

    private final SupportService supportService;

    @PostMapping("/tickets")
    @Operation(summary = "Create a support ticket")
    public ResponseEntity<ApiResponse<SupportTicketDTO>> createTicket(
            @Valid @RequestBody SupportTicketCreateRequest request) {

        SupportTicketDTO ticket = supportService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ticket created! We'll get back to you soon.", ticket));
    }

    @GetMapping("/tickets")
    @Operation(summary = "Get my support tickets")
    public ResponseEntity<ApiResponse<PagedResponse<SupportTicketDTO>>> getMyTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<SupportTicketDTO> tickets = supportService.getMyTickets(page, size);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    @GetMapping("/tickets/{ticketId}")
    @Operation(summary = "Get ticket details")
    public ResponseEntity<ApiResponse<SupportTicketDTO>> getTicketById(
            @PathVariable UUID ticketId) {

        SupportTicketDTO ticket = supportService.getTicketById(ticketId);
        return ResponseEntity.ok(ApiResponse.success(ticket));
    }
}