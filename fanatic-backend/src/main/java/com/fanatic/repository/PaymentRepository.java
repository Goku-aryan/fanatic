package com.fanatic.repository;

import com.fanatic.entity.Payment;
import com.fanatic.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // ========== FIND ==========
    Optional<Payment> findByProviderSessionId(String sessionId);

    Optional<Payment> findByProviderPaymentId(String paymentId);

    // ========== FIND BY USER ==========
    Page<Payment> findByUserId(UUID userId, Pageable pageable);

    Page<Payment> findByUserIdAndPaymentStatus(UUID userId, PaymentStatus status, Pageable pageable);

    // ========== FIND BY STATUS ==========
    Page<Payment> findByPaymentStatus(PaymentStatus status, Pageable pageable);

    // ========== CHECK IF USER PAID FOR CONTENT ==========
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Payment p " +
           "WHERE p.user.id = :userId AND p.content.id = :contentId AND p.paymentStatus = 'COMPLETED'")
    boolean hasUserPaidForContent(@Param("userId") UUID userId, @Param("contentId") UUID contentId);

    // ========== TOTAL REVENUE ==========
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentStatus = 'COMPLETED'")
    BigDecimal getTotalRevenue();

    // ========== REVENUE BY DATE RANGE ==========
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.paymentStatus = 'COMPLETED' AND p.createdAt >= :since")
    BigDecimal getRevenueSince(@Param("since") ZonedDateTime since);

    // ========== COUNT ==========
    long countByPaymentStatus(PaymentStatus status);

    // ========== RECENT PAYMENTS (Admin) ==========
    @Query("SELECT p FROM Payment p ORDER BY p.createdAt DESC")
    Page<Payment> findRecentPayments(Pageable pageable);

    // ========== REVENUE OVER TIME (Admin Charts) ==========
    @Query("SELECT CAST(p.createdAt AS date) as date, SUM(p.amount) as total " +
           "FROM Payment p WHERE p.paymentStatus = 'COMPLETED' AND p.createdAt >= :since " +
           "GROUP BY CAST(p.createdAt AS date) ORDER BY CAST(p.createdAt AS date)")
    List<Object[]> getRevenueOverTime(@Param("since") ZonedDateTime since);

    // ========== PAYMENTS BY CONTENT ==========
    @Query("SELECT p FROM Payment p WHERE p.content.id = :contentId ORDER BY p.createdAt DESC")
    Page<Payment> findByContentId(@Param("contentId") UUID contentId, Pageable pageable);
}