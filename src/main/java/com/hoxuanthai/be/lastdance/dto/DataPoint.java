package com.hoxuanthai.be.lastdance.dto;

import com.hoxuanthai.be.lastdance.model.DataType;
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
}
