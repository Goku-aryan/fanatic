package com.fanatic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "early_access_grants")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EarlyAccessGrant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;
    
    @Column(name = "granted_at")
    private ZonedDateTime grantedAt;
    
    @Column(name = "expires_at")
    private ZonedDateTime expiresAt;
    
    @PrePersist
    void prePersist() {
        if (grantedAt == null) grantedAt = ZonedDateTime.now();
    }
}