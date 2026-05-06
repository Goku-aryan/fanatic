package com.fanatic.controller;

import com.fanatic.dto.admin.AdminDashboardDTO;
import com.fanatic.dto.common.ApiResponse;
import com.fanatic.dto.common.PagedResponse;
import com.fanatic.dto.content.ContentCreateRequest;
import com.fanatic.dto.content.ContentDTO;
import com.fanatic.dto.payment.PaymentDTO;
import com.fanatic.dto.support.SupportTicketDTO;
import com.fanatic.dto.support.TicketResponseRequest;
import com.fanatic.dto.user.UserDTO;
import com.fanatic.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin Panel", description = "Admin APIs - Dashboard, Controls, Stats")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final ContentService contentService;
    private final PaymentService paymentService;
    private final SupportService supportService;

    // ==========================================
    // DASHBOARD
    // ==========================================

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard stats")
    public ResponseEntity<ApiResponse<AdminDashboardDTO>> getDashboard() {
        AdminDashboardDTO dashboard = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueStats() {
        Map<String, Object> stats = adminService.getRevenueStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    // ==========================================
    // USER MANAGEMENT
    // ==========================================

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<UserDTO> users = userService.getAllUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PatchMapping("/users/{userId}/toggle-active")
    @Operation(summary = "Activate/Deactivate user")
    public ResponseEntity<ApiResponse<UserDTO>> toggleUserActive(
            @PathVariable UUID userId) {

        UserDTO user = userService.toggleUserActive(userId);
        return ResponseEntity.ok(ApiResponse.success("User status updated!", user));
    }

    @PatchMapping("/users/{userId}/role")
    @Operation(summary = "Change user role")
    public ResponseEntity<ApiResponse<UserDTO>> changeUserRole(
            @PathVariable UUID userId,
            @RequestParam String role) {

        UserDTO user = userService.changeUserRole(userId, role);
        return ResponseEntity.ok(ApiResponse.success("Role updated!", user));
    }

    // ==========================================
    // CONTENT MANAGEMENT
    // ==========================================

    @PostMapping("/content")
    @Operation(summary = "Create new content")
    public ResponseEntity<ApiResponse<ContentDTO>> createContent(
            @Valid @RequestBody ContentCreateRequest request) {

        ContentDTO content = contentService.createContent(request);
        return ResponseEntity.ok(ApiResponse.success("Content created!", content));
    }

    @PutMapping("/content/{contentId}")
    @Operation(summary = "Update content")
    public ResponseEntity<ApiResponse<ContentDTO>> updateContent(
            @PathVariable UUID contentId,
            @Valid @RequestBody ContentCreateRequest request) {

        ContentDTO content = contentService.updateContent(contentId, request);
        return ResponseEntity.ok(ApiResponse.success("Content updated!", content));
    }

    @DeleteMapping("/content/{contentId}")
    @Operation(summary = "Delete content")
    public ResponseEntity<ApiResponse<Void>> deleteContent(
            @PathVariable UUID contentId) {

        contentService.deleteContent(contentId);
        return ResponseEntity.ok(ApiResponse.success("Content deleted!", null));
    }

    // ==========================================
    // PAYMENT MANAGEMENT
    // ==========================================

    @GetMapping("/payments")
    @Operation(summary = "Get all payments")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentDTO>>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<PaymentDTO> payments = paymentService.getAllPayments(page, size);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    // ==========================================
    // SUPPORT TICKET MANAGEMENT
    // ==========================================

    @GetMapping("/tickets")
    @Operation(summary = "Get all support tickets")
    public ResponseEntity<ApiResponse<PagedResponse<SupportTicketDTO>>> getAllTickets(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<SupportTicketDTO> tickets = supportService.getAllTickets(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    @PostMapping("/tickets/{ticketId}/respond")
    @Operation(summary = "Respond to a support ticket")
    public ResponseEntity<ApiResponse<SupportTicketDTO>> respondToTicket(
            @PathVariable UUID ticketId,
            @Valid @RequestBody TicketResponseRequest request) {

        SupportTicketDTO ticket = supportService.respondToTicket(ticketId, request);
        return ResponseEntity.ok(ApiResponse.success("Response sent!", ticket));
    }

    @PatchMapping("/tickets/{ticketId}/status")
    @Operation(summary = "Update ticket status")
    public ResponseEntity<ApiResponse<SupportTicketDTO>> updateTicketStatus(
            @PathVariable UUID ticketId,
            @RequestParam String status) {

        SupportTicketDTO ticket = supportService.updateTicketStatus(ticketId, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated!", ticket));
    }

    // ==========================================
    // SITE SETTINGS
    // ==========================================

    @GetMapping("/settings")
    @Operation(summary = "Get all site settings")
    public ResponseEntity<ApiResponse<Map<String, String>>> getSettings() {
        Map<String, String> settings = adminService.getSiteSettings();
        return ResponseEntity.ok(ApiResponse.success(settings));
    }

    @PutMapping("/settings")
    @Operation(summary = "Update a site setting")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateSetting(
            @RequestParam String key,
            @RequestParam String value) {

        Map<String, String> settings = adminService.updateSiteSetting(key, value);
        return ResponseEntity.ok(ApiResponse.success("Setting updated!", settings));
    }
}