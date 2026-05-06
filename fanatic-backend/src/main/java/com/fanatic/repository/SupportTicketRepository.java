package com.fanatic.repository;

import com.fanatic.entity.SupportTicket;
import com.fanatic.entity.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {

    // ========== FIND BY USER ==========
    Page<SupportTicket> findByUserId(UUID userId, Pageable pageable);

    Page<SupportTicket> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    // ========== FIND BY STATUS ==========
    Page<SupportTicket> findByStatus(TicketStatus status, Pageable pageable);

    Page<SupportTicket> findByStatusOrderByCreatedAtAsc(TicketStatus status, Pageable pageable);

    // ========== COUNT ==========
    long countByStatus(TicketStatus status);

    long countByUserId(UUID userId);

    // ========== FIND ALL ORDERED ==========
    @Query("SELECT t FROM SupportTicket t ORDER BY " +
           "CASE t.status " +
           "WHEN 'OPEN' THEN 1 " +
           "WHEN 'IN_PROGRESS' THEN 2 " +
           "WHEN 'RESOLVED' THEN 3 " +
           "WHEN 'CLOSED' THEN 4 END, " +
           "t.createdAt ASC")
    Page<SupportTicket> findAllOrderedByPriority(Pageable pageable);

    // ========== FIND BY USER AND STATUS ==========
    Page<SupportTicket> findByUserIdAndStatus(UUID userId, TicketStatus status, Pageable pageable);
}