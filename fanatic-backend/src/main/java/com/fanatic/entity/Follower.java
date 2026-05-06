package com.fanatic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "followers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Follower {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User followerUser;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private User followingUser;
    
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
    
    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = ZonedDateTime.now();
    }
}