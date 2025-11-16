package com.hoxuanthai.be.lastdance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDto {
    private Long id;
    @NotNull
    @NotBlank
    private String deviceUuid;
    @NotNull
    @NotBlank
    private String deviceName;
    private LocalDateTime lastSyncAt;
    private boolean isActive;
    @NotNull
    private Long userId;
    private LocalDateTime createdAt;
}
