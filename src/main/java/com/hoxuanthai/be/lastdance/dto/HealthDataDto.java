package com.hoxuanthai.be.lastdance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthDataDto {

    @NotNull
    @NotBlank
    String deviceUuid;
    
    @NotNull
    List<DataPoint> dataPoints;
}
