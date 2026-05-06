package com.fanatic.dto.admin;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardDTO {
    private Long totalUsers;
    private Long activeUsers;
    private Long totalContent;
    private Long totalMovies;
    private Long totalSeries;
    private Long totalBooks;
    private Long totalReviews;
    private Long totalPayments;
    private BigDecimal totalRevenue;
    private Long openTickets;
    private Long verifiedUsers;
    private List<Map<String, Object>> recentUsers;
    private List<Map<String, Object>> recentPayments;
    private List<Map<String, Object>> userGrowth;
    private List<Map<String, Object>> contentDistribution;
}