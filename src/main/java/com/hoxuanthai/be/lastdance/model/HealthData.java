package com.hoxuanthai.be.lastdance.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="health_data")
public class HealthData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name="timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name="heart_rate")
    private Integer heartRate;

    @Column(name="steps_count")
    private Integer stepsCount;

    @Column(name="spo2_percent")
    private Double spo2Percent;
}
