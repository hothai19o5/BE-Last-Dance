package com.hoxuanthai.be.lastdance.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOverviewDto {

    private Long userId;

    private String username;

    private String email;

    // Aggregated statistics only - no raw health data
    private HealthSummary healthSummary;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthSummary {
        // Last 7 days averages
        private Double avgHeartRate;
        private Double avgSpO2;
        private Double avgSteps;
        private Double avgCalories;
        private Double avgWaterIntakeMl;
        private Double avgSleepMinutes;

        // Total devices
        private Integer totalDevices;
        private Integer activeDevices;

        // Alert statistics
        private Integer totalAlerts;
        private Integer highSeverityAlerts;

        // Last sync timestamp
        private String lastSyncTime;
    }
}
