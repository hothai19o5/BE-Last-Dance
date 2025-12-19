package com.hoxuanthai.be.lastdance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "HEALTH_DATA")
@EqualsAndHashCode(callSuper = true)
public class HealthData extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "steps_count")
    private Integer stepsCount;

    @Column(name = "spo2_percent")
    private Double spo2Percent;
}
