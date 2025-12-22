package com.hoxuanthai.be.lastdance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private Long id;

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
}