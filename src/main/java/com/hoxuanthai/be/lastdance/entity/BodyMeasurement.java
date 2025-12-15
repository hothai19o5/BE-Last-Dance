package com.hoxuanthai.be.lastdance.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created on November 2025
 *
 * @author HoXuanThai
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Body_Measurements")
public class BodyMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(name="weight_kg")
    private Double weightKg;
    
    @Column(name="height_m")
    private Double heightM;

    @Column(name="bmi")
    private Double bmi;

    @Column(name="recorded_at")
    private LocalDateTime recordedAt;
}
