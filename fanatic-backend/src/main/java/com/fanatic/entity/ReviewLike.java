package com.fanatic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "review_likes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ReviewLike {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;
    
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
    
    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = ZonedDateTime.now();
    }
}