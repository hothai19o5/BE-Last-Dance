package com.hoxuanthai.be.lastdance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "HEALTH_DATA")
public class HealthData {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Id
    private LocalDateTime timestamp;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "steps_count")
    private Integer stepsCount;

    @Column(name = "spo2_percent")
    private Double spo2Percent;
}
