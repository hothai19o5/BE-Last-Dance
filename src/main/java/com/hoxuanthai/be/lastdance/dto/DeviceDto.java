package com.hoxuanthai.be.lastdance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private boolean isActive;

    private Long userId;

    @NotNull
    @NotBlank
    private String username;
}
