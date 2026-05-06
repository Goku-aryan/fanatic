package com.fanatic.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "level_thresholds")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LevelThreshold {
    
    @Id
    private Integer level;
    
    @Column(name = "points_required", nullable = false)
    private Integer pointsRequired;
    
    @Column(nullable = false)
    private String title;
    
    private String icon;
}