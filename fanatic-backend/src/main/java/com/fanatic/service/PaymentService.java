package com.fanatic.service;

import com.fanatic.config.StripeConfig;
import com.fanatic.dto.common.PagedResponse;
import com.fanatic.dto.payment.CheckoutResponse;
import com.fanatic.dto.payment.PaymentDTO;
import com.fanatic.entity.*;
import com.fanatic.exception.BadRequestException;
import com.fanatic.exception.ResourceNotFoundException;
import com.fanatic.repository.*;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ContentRepository contentRepository;
    private final EarlyAccessGrantRepository earlyAccessGrantRepository;
    private final UserService userService;
    private final StripeConfig stripeConfig;

    // ========== CREATE CHECKOUT SESSION ==========
    @Transactional
    public CheckoutResponse createCheckoutSession(UUID contentId) {
        User user = userService.getCurrentUser();

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));

        if (!content.getIsEarlyAccess()) {
            throw new BadRequestException("This content is not available for early access");
        }

        if (earlyAccessGrantRepository.existsByUserIdAndContentId(user.getId(), contentId)) {
            throw new BadRequestException("You already have early access to this content");
        }

        BigDecimal amount = content.getEarlyAccessPrice();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Invalid price for early access");
        }

        // Create payment record
        Payment payment = Payment.builder()
                .user(user)
                .content(content)
                .amount(amount)
                .currency("USD")
                .paymentStatus(PaymentStatus.PENDING)
                .paymentProvider("stripe")
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        try {
            // Create Stripe checkout session
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(stripeConfig.getSuccessUrl() +
                            "?session_id={CHECKOUT_SESSION_ID}&payment_id=" + savedPayment.getId())
                    .setCancelUrl(stripeConfig.getCancelUrl() +
                            "?payment_id=" + savedPayment.getId())
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setUnitAmount(amount.multiply(new BigDecimal("100")).longValue())
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("🎬 First Look Pass: " + content.getTitle())
                                            .setDescription("Early access to review " +
                                                    content.getContentType().name().toLowerCase() +
                                                    " before public launch")
                                            .build())
                                    .build())
                            .build())
                    .putMetadata("payment_id", savedPayment.getId().toString())
                    .putMetadata("user_id", user.getId().toString())
                    .putMetadata("content_id", contentId.toString())
                    .setCustomerEmail(user.getEmail())
                    .build();

            Session session = Session.create(params);

            // Update payment with session ID
            savedPayment.setProviderSessionId(session.getId());
            paymentRepository.save(savedPayment);

            return CheckoutResponse.builder()
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .publishableKey(stripeConfig.getPublishableKey())
                    .build();

        } catch (StripeException e) {
            log.error("Stripe error: {}", e.getMessage());
            savedPayment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(savedPayment);
            throw new BadRequestException("Payment processing failed. Please try again.");
        }
    }

    // ========== CONFIRM PAYMENT (after Stripe redirect) ==========
    @Transactional
    public PaymentDTO confirmPayment(String sessionId, UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        try {
            Session session = Session.retrieve(sessionId);

            if ("complete".equals(session.getStatus()) &&
                "paid".equals(session.getPaymentStatus())) {

                payment.setPaymentStatus(PaymentStatus.COMPLETED);
                payment.setProviderPaymentId(session.getPaymentIntent());
                paymentRepository.save(payment);

                // Grant early access
                EarlyAccessGrant grant = EarlyAccessGrant.builder()
                        .user(payment.getUser())
                        .content(payment.getContent())
                        .payment(payment)
                        .expiresAt(payment.getContent().getEarlyAccessEnd() != null ?
                                payment.getContent().getEarlyAccessEnd()
                                        .atStartOfDay()
                                        .atZone(java.time.ZoneId.systemDefault()) : null)
                        .build();

                earlyAccessGrantRepository.save(grant);

                log.info("Early access granted to user {} for content {}",
                        payment.getUser().getId(), payment.getContent().getId());

            } else {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            }

        } catch (StripeException e) {
            log.error("Error confirming payment: {}", e.getMessage());
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }

        return mapToPaymentDTO(payment);
    }

    // ========== HANDLE STRIPE WEBHOOK ==========
    @Transactional
    public void handleWebhook(String sessionId) {
        Payment payment = paymentRepository.findByProviderSessionId(sessionId)
                .orElse(null);

        if (payment == null) {
            log.warn("No payment found for session: {}", sessionId);
            return;
        }

        try {
            Session session = Session.retrieve(sessionId);

            if ("complete".equals(session.getStatus()) &&
                "paid".equals(session.getPaymentStatus())) {

                if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
                    payment.setPaymentStatus(PaymentStatus.COMPLETED);
                    payment.setProviderPaymentId(session.getPaymentIntent());
                    paymentRepository.save(payment);

                    // Grant early access if not already granted
                    if (!earlyAccessGrantRepository.existsByUserIdAndContentId(
                            payment.getUser().getId(), payment.getContent().getId())) {

                        EarlyAccessGrant grant = EarlyAccessGrant.builder()
                                .user(payment.getUser())
                                .content(payment.getContent())
                                .payment(payment)
                                .build();

                        earlyAccessGrantRepository.save(grant);
                    }
                }
            }
        } catch (StripeException e) {
            log.error("Webhook processing error: {}", e.getMessage());
        }
    }

    // ========== GET USER PAYMENTS ==========
    @Transactional(readOnly = true)
    public PagedResponse<PaymentDTO> getUserPayments(int page, int size) {
        UUID userId = userService.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Payment> payments = paymentRepository.findByUserId(userId, pageable);
        return mapToPagedResponse(payments);
    }

    // ========== GET ALL PAYMENTS (Admin) ==========
    @Transactional(readOnly = true)
    public PagedResponse<PaymentDTO> getAllPayments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Payment> payments = paymentRepository.findAll(pageable);
        return mapToPagedResponse(payments);
    }

    // ========== GET PAYMENT BY ID ==========
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        return mapToPaymentDTO(payment);
    }

    // ========== MAP TO DTO ==========
    public PaymentDTO mapToPaymentDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .userId(payment.getUser().getId())
                .contentId(payment.getContent().getId())
                .contentTitle(payment.getContent().getTitle())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentStatus(payment.getPaymentStatus().name())
                .paymentProvider(payment.getPaymentProvider())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    // ========== MAP TO PAGED RESPONSE ==========
    private PagedResponse<PaymentDTO> mapToPagedResponse(Page<Payment> page) {
        List<PaymentDTO> content = page.getContent().stream()
                .map(this::mapToPaymentDTO)
                .collect(Collectors.toList());

        return PagedResponse.<PaymentDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}