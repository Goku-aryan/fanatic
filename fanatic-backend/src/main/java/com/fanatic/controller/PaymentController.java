package com.fanatic.controller;

import com.fanatic.dto.common.ApiResponse;
import com.fanatic.dto.common.PagedResponse;
import com.fanatic.dto.payment.CheckoutRequest;
import com.fanatic.dto.payment.CheckoutResponse;
import com.fanatic.dto.payment.PaymentDTO;
import com.fanatic.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "First Look Pass - Early Access Payment APIs")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    @Operation(summary = "Create checkout session for First Look Pass")
    public ResponseEntity<ApiResponse<CheckoutResponse>> createCheckout(
            @Valid @RequestBody CheckoutRequest request) {

        CheckoutResponse response = paymentService.createCheckoutSession(request.getContentId());
        return ResponseEntity.ok(ApiResponse.success("Checkout session created!", response));
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirm payment after Stripe redirect")
    public ResponseEntity<ApiResponse<PaymentDTO>> confirmPayment(
            @RequestParam String sessionId,
            @RequestParam UUID paymentId) {

        PaymentDTO payment = paymentService.confirmPayment(sessionId, paymentId);
        return ResponseEntity.ok(ApiResponse.success("Payment confirmed!", payment));
    }

    @GetMapping("/my-payments")
    @Operation(summary = "Get my payment history")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentDTO>>> getMyPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<PaymentDTO> payments = paymentService.getUserPayments(page, size);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment details")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentById(
            @PathVariable UUID paymentId) {

        PaymentDTO payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }
}