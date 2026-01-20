package com.hoxuanthai.be.lastdance.service;

import com.hoxuanthai.be.lastdance.dto.UserOverviewDto;
import com.hoxuanthai.be.lastdance.entity.User;
import com.hoxuanthai.be.lastdance.exceptions.ResourceNotFoundException;
import com.hoxuanthai.be.lastdance.repository.DeviceRepository;
import com.hoxuanthai.be.lastdance.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final EntityManager entityManager;

    @Override
    public UserOverviewDto getUserOverview(Long userId) {
        // Get user info
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Calculate time range (last 7 days)
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(7);

        // Query for aggregated health statistics
        String sql = "SELECT " +
                "AVG(hd.heart_rate) AS avg_hr, " +
                "AVG(hd.spo2) AS avg_spo2, " +
                "AVG(hd.step_count) AS avg_steps, " +
                "AVG(hd.calories_burned) AS avg_calories, " +
                "AVG(hd.water_intake_ml) AS avg_water, " +
                "AVG(hd.sleep_duration_minutes) AS avg_sleep, " +
                "MAX(hd.timestamp) AS last_sync " +
                "FROM health_data hd " +
                "JOIN devices d ON hd.device_id = d.id " +
                "WHERE d.user_id = :userId " +
                "AND hd.timestamp >= :startTime " +
                "AND hd.timestamp <= :endTime";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("userId", userId);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);

        Object[] result = (Object[]) query.getSingleResult();

        // Get device statistics
        Long totalDevices = deviceRepository.countByUserId(userId);
        Long activeDevices = deviceRepository.countActiveDevicesByUserId(userId);

        // Count alerts (assuming alert_score >= 0.5 is considered an alert)
        String alertSql = "SELECT " +
                "COUNT(*) AS total_alerts, " +
                "COUNT(CASE WHEN hd.alert_score >= 0.8 THEN 1 END) AS high_severity_alerts " +
                "FROM health_data hd " +
                "JOIN devices d ON hd.device_id = d.id " +
                "WHERE d.user_id = :userId " +
                "AND hd.timestamp >= :startTime " +
                "AND hd.timestamp <= :endTime " +
                "AND hd.alert_score >= 0.5";

        Query alertQuery = entityManager.createNativeQuery(alertSql);
        alertQuery.setParameter("userId", userId);
        alertQuery.setParameter("startTime", startTime);
        alertQuery.setParameter("endTime", endTime);

        Object[] alertResult = (Object[]) alertQuery.getSingleResult();

        // Build HealthSummary
        UserOverviewDto.HealthSummary healthSummary = UserOverviewDto.HealthSummary.builder()
                .avgHeartRate(result[0] != null ? ((Number) result[0]).doubleValue() : 0.0)
                .avgSpO2(result[1] != null ? ((Number) result[1]).doubleValue() : 0.0)
                .avgSteps(result[2] != null ? ((Number) result[2]).doubleValue() : 0.0)
                .avgCalories(result[3] != null ? ((Number) result[3]).doubleValue() : 0.0)
                .avgWaterIntakeMl(result[4] != null ? ((Number) result[4]).doubleValue() : 0.0)
                .avgSleepMinutes(result[5] != null ? ((Number) result[5]).doubleValue() : 0.0)
                .totalDevices(totalDevices != null ? totalDevices.intValue() : 0)
                .activeDevices(activeDevices != null ? activeDevices.intValue() : 0)
                .totalAlerts(alertResult[0] != null ? ((Number) alertResult[0]).intValue() : 0)
                .highSeverityAlerts(alertResult[1] != null ? ((Number) alertResult[1]).intValue() : 0)
                .lastSyncTime(result[6] != null ? ((java.sql.Timestamp) result[6]).toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "Never")
                .build();

        return UserOverviewDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .healthSummary(healthSummary)
                .build();
    }
}
