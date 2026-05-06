package com.fanatic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_badges")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ✅ FIX
    @Enumerated(EnumType.STRING)
    @Column(name = "badge_type", columnDefinition = "varchar(30)")
    private BadgeType badgeType;

    @Column(name = "badge_name")
    private String badgeName;

    @Column(name = "badge_icon")
    private String badgeIcon;

    @Column(name = "awarded_at")
    private ZonedDateTime awardedAt;

    @PrePersist
    void prePersist() {
        if (awardedAt == null) awardedAt = ZonedDateTime.now();
    }
}