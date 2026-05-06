package com.fanatic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "site_settings")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SiteSetting {
    
    @Id
    private String key;
    
    @Column(nullable = false)
    private String value;
    
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
}