package com.fanatic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_content")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserContent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;
    
    private BigDecimal rating;
    
    @Builder.Default
    private String status = "completed";
    
    @Column(name = "points_earned")
    @Builder.Default
    private Integer pointsEarned = 0;
    
    @Column(name = "added_at")
    private ZonedDateTime addedAt;
    
    @PrePersist
    void prePersist() {
        if (addedAt == null) addedAt = ZonedDateTime.now();
    }
}