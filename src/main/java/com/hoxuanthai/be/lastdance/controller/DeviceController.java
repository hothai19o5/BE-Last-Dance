package com.hoxuanthai.be.lastdance.controller;

import com.hoxuanthai.be.lastdance.dto.DeviceDto;
import com.hoxuanthai.be.lastdance.dto.HealthDataDto;
import com.hoxuanthai.be.lastdance.dto.StatisticsDto;
import com.hoxuanthai.be.lastdance.dto.response.PageResponse;
import com.hoxuanthai.be.lastdance.ratelimit.KeyType;
import com.hoxuanthai.be.lastdance.ratelimit.RateLimit;
import com.hoxuanthai.be.lastdance.ratelimit.RateLimitType;
import com.hoxuanthai.be.lastdance.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping("/devices")
    @Operation(tags = "Device Service", description = "Get all devices registered.")
    ResponseEntity<BaseResponse<PageResponse<DeviceDto>>> getAllDevices(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy) {
        Page<DeviceDto> devicePage = deviceService.getAllDevices(page, size, sortBy);

        PageResponse<DeviceDto> pageResponse = PageResponse.<DeviceDto>builder()
                .content(devicePage.getContent())
                .page(devicePage.getNumber())
                .size(devicePage.getSize())
                .totalElements(devicePage.getTotalElements())
                .totalPages(devicePage.getTotalPages())
                .first(devicePage.isFirst())
                .last(devicePage.isLast())
                .build();
        return BaseResponse.success(pageResponse);
    }

    @PostMapping("/device")
    @Operation(tags = "Device Service", description = "Register a new device to the system.")
    ResponseEntity<BaseResponse<DeviceDto>> registerDevice(@RequestBody @Valid DeviceDto deviceDto) {
        DeviceDto device = deviceService.registerDevice(deviceDto);
        return BaseResponse.created(device);
    }

    @PutMapping("/devices/{deviceId}/config")
    @Operation(tags = "Device Service", description = "Update device configuration.")
    ResponseEntity<BaseResponse<DeviceDto>> updateDeviceConfig(@RequestBody DeviceDto deviceDto) {
        DeviceDto result = deviceService.updateDevice(deviceDto);
        return BaseResponse.success(result);
    }

    @PostMapping("/sync/health-data")
    @RateLimit(type = RateLimitType.UPLOAD_HEALTH_DATA, keyBy = KeyType.TOKEN)
    @Operation(tags = "Device Service", description = "Sync health data from device.")
    ResponseEntity<BaseResponse<String>> syncHealthData(@RequestBody @Valid HealthDataDto healthDataDto) {
        deviceService.syncHealthData(healthDataDto);
        return BaseResponse.success(null, "Sync data successfully!");
    }

    @DeleteMapping("/device/{deviceUuid}")
    @Operation(tags = "Device Service", description = "User remove device.")
    ResponseEntity<BaseResponse<String>> removeDevice(@PathVariable String deviceUuid) {
        deviceService.removeDevice(deviceUuid);
        return BaseResponse.success(null, "Device removed successfully!");
    }

    @GetMapping("/health-data/statistics")
    @Operation(tags = "Device Service", description = "Get aggregated health statistics.")
    ResponseEntity<BaseResponse<StatisticsDto>> getHealthStatistics(
            @RequestParam String metric,
            @RequestParam String range) {
        StatisticsDto stats = deviceService.getHealthStatistics(metric, range);
        return BaseResponse.success(stats);
    }
}
