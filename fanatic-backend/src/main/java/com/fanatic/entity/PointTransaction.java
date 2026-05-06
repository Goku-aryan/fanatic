package com.fanatic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "point_transactions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PointTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private Integer points;
    
    @Column(nullable = false)
    private String reason;
    
    @Column(name = "reference_type")
    private String referenceType;
    
    @Column(name = "reference_id")
    private UUID referenceId;
    
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
    
    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = ZonedDateTime.now();
    }
}