package com.hoxuanthai.be.lastdance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@IdClass(HealthDataId.class)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "HEALTH_DATA")
public class HealthData {

    @Id
    @GeneratedValue
    private UUID id;

    @Id
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "steps_count")
    private Integer stepsCount;

    @Column(name = "spo2_percent")
    private Double spo2Percent;

    @Column(name = "calories_burned")
    private Double caloriesBurned;

    @Column(name = "water_intake_ml")
    private Integer waterIntakeMl;

    @Column(name = "activity_status")
    private Integer activityStatus; // 0=sleeping, 1=resting, 2=walking, 3=running

    @Column(name = "sleep_duration_minutes")
    private Integer sleepDurationMinutes;
}