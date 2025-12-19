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
}
