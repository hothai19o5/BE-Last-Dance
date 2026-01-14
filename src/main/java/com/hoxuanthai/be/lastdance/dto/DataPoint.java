package com.hoxuanthai.be.lastdance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataPoint {

    @NotNull
    private LocalDateTime timestamp;

    private Integer heartRate;

    private Double spo2;

    private Integer stepCount;

    private Double caloriesBurned;

    private Integer waterIntakeMl;

    private Integer activityStatus; // 0=sleeping, 1=resting, 2=walking, 3=running

    private Integer sleepDurationMinutes;

    private Double alertScore; // ML anomaly detection score (0.0-1.0), null if not available
}
