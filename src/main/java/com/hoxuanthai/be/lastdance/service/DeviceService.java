package com.hoxuanthai.be.lastdance.service;

import com.hoxuanthai.be.lastdance.dto.DeviceDto;
import com.hoxuanthai.be.lastdance.dto.HealthDataDto;

import java.time.LocalDateTime;
import java.util.List;

public interface DeviceService {

    DeviceDto registerDevice(DeviceDto deviceDto);

    DeviceDto updateDevice(DeviceDto deviceDto);

    List<DeviceDto> getAllDevicesByUserId(Long userId);

    boolean syncHealthData(HealthDataDto healthDataDto);

    HealthDataDto getHealthData(Long userId, String deviceUuid, LocalDateTime startDate, LocalDateTime endDate);
}
