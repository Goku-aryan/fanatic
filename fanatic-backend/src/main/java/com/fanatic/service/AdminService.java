package com.fanatic.service;

import com.fanatic.dto.admin.AdminDashboardDTO;
import com.fanatic.entity.*;
import com.fanatic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final SiteSettingRepository siteSettingRepository;

    // ========== GET DASHBOARD STATS ==========
    @Transactional(readOnly = true)
    public AdminDashboardDTO getDashboardStats() {

        // User stats
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActive(true);
        long verifiedUsers = userRepository.countByIsVerified(true);

        // Content stats
        long totalContent = contentRepository.count();
        long totalMovies = contentRepository.countByContentType(ContentType.MOVIE);
        long totalSeries = contentRepository.countByContentType(ContentType.SERIES);
        long totalBooks = contentRepository.countByContentType(ContentType.BOOK);

        // Review stats
        long totalReviews = reviewRepository.count();

        // Payment stats
        long totalPayments = paymentRepository.countByPaymentStatus(PaymentStatus.COMPLETED);
        BigDecimal totalRevenue = paymentRepository.getTotalRevenue();

        // Support stats
        long openTickets = supportTicketRepository.countByStatus(TicketStatus.OPEN);

        // Recent users (last 10)
        List<Map<String, Object>> recentUsers = userRepository
                .findRecentUsers(PageRequest.of(0, 10))
                .getContent().stream()
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("username", u.getUsername());
                    map.put("email", u.getEmail());
                    map.put("role", u.getRole().name());
                    map.put("points", u.getPoints());
                    map.put("level", u.getLevel());
                    map.put("isActive", u.getIsActive());
                    map.put("createdAt", u.getCreatedAt());
                    return map;
                })
                .collect(Collectors.toList());

        // Recent payments (last 10)
        List<Map<String, Object>> recentPayments = paymentRepository
                .findRecentPayments(PageRequest.of(0, 10))
                .getContent().stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("username", p.getUser().getUsername());
                    map.put("contentTitle", p.getContent().getTitle());
                    map.put("amount", p.getAmount());
                    map.put("status", p.getPaymentStatus().name());
                    map.put("createdAt", p.getCreatedAt());
                    return map;
                })
                .collect(Collectors.toList());

        // User growth (last 30 days)
        ZonedDateTime thirtyDaysAgo = ZonedDateTime.now().minusDays(30);
        List<Object[]> growthData = userRepository.getUserGrowth(thirtyDaysAgo);
        List<Map<String, Object>> userGrowth = growthData.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", row[0].toString());
                    map.put("count", row[1]);
                    return map;
                })
                .collect(Collectors.toList());

        // Content distribution
        List<Object[]> distData = contentRepository.getContentDistribution();
        List<Map<String, Object>> contentDistribution = distData.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("type", row[0].toString());
                    map.put("count", row[1]);
                    return map;
                })
                .collect(Collectors.toList());

        return AdminDashboardDTO.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .verifiedUsers(verifiedUsers)
                .totalContent(totalContent)
                .totalMovies(totalMovies)
                .totalSeries(totalSeries)
                .totalBooks(totalBooks)
                .totalReviews(totalReviews)
                .totalPayments(totalPayments)
                .totalRevenue(totalRevenue)
                .openTickets(openTickets)
                .recentUsers(recentUsers)
                .recentPayments(recentPayments)
                .userGrowth(userGrowth)
                .contentDistribution(contentDistribution)
                .build();
    }

    // ========== GET SITE SETTINGS ==========
    @Transactional(readOnly = true)
    public Map<String, String> getSiteSettings() {
        List<SiteSetting> settings = siteSettingRepository.findAll();
        Map<String, String> settingsMap = new HashMap<>();
        settings.forEach(s -> settingsMap.put(s.getKey(), s.getValue()));
        return settingsMap;
    }

    // ========== UPDATE SITE SETTING ==========
    @Transactional
    public Map<String, String> updateSiteSetting(String key, String value) {
        SiteSetting setting = siteSettingRepository.findByKey(key)
                .orElse(SiteSetting.builder().key(key).build());

        setting.setValue(value);
        setting.setUpdatedAt(ZonedDateTime.now());
        siteSettingRepository.save(setting);

        return getSiteSettings();
    }

    // ========== REVENUE STATS ==========
    @Transactional(readOnly = true)
    public Map<String, Object> getRevenueStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalRevenue", paymentRepository.getTotalRevenue());
        stats.put("last7Days", paymentRepository.getRevenueSince(ZonedDateTime.now().minusDays(7)));
        stats.put("last30Days", paymentRepository.getRevenueSince(ZonedDateTime.now().minusDays(30)));
        stats.put("completedPayments", paymentRepository.countByPaymentStatus(PaymentStatus.COMPLETED));
        stats.put("pendingPayments", paymentRepository.countByPaymentStatus(PaymentStatus.PENDING));
        stats.put("failedPayments", paymentRepository.countByPaymentStatus(PaymentStatus.FAILED));

        // Revenue over time (last 30 days)
        List<Object[]> revenueData = paymentRepository
                .getRevenueOverTime(ZonedDateTime.now().minusDays(30));
        List<Map<String, Object>> revenueOverTime = revenueData.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", row[0].toString());
                    map.put("revenue", row[1]);
                    return map;
                })
                .collect(Collectors.toList());
        stats.put("revenueOverTime", revenueOverTime);

        return stats;
    }
}