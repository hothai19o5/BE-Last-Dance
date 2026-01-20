package com.hoxuanthai.be.lastdance.service;

import com.hoxuanthai.be.lastdance.dto.DeviceDto;
import com.hoxuanthai.be.lastdance.dto.HealthDataDto;
import com.hoxuanthai.be.lastdance.dto.StatisticsDto;
import com.hoxuanthai.be.lastdance.dto.response.DevicesStats;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface DeviceService {

    DeviceDto registerDevice(DeviceDto deviceDto);

    DeviceDto updateDevice(DeviceDto deviceDto);

    List<DeviceDto> getAllDevicesByUserId(Long userId);

    /**
     * Get all devices of the current authenticated user
     */
    List<DeviceDto> getMyDevices();

    void syncHealthData(HealthDataDto healthDataDto);

    HealthDataDto getHealthData(Long userId, String deviceUuid, LocalDateTime startDate, LocalDateTime endDate);

    Page<DeviceDto> getAllDevices(int page, int size, String sortBy);

    DevicesStats getDevicesStats();

    void removeDevice(String deviceUuid);

    StatisticsDto getHealthStatistics(String metric, String range);
}
