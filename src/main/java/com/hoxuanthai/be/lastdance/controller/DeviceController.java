package com.hoxuanthai.be.lastdance.controller;

import com.hoxuanthai.be.lastdance.dto.DeviceDto;
import com.hoxuanthai.be.lastdance.dto.HealthDataDto;
import com.hoxuanthai.be.lastdance.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/devices")
    @PreAuthorize("hasRole('USER')")
    @Operation(tags = "Device Service", description = "Get all devices registered to the user.")
    ResponseEntity<List<DeviceDto>> getAllDevices(@RequestParam Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        List<DeviceDto> result = deviceService.getAllDevices(userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/device")
    @PreAuthorize("hasRole('USER')")
    @Operation(tags = "Device Service", description = "Register a new device to the system.")
    ResponseEntity<DeviceDto> registerDevice(@RequestBody @Valid DeviceDto deviceDto) {
        DeviceDto device = deviceService.registerDevice(deviceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(device);
    }

    @PutMapping("/devices/{deviceId}/config")
    @PreAuthorize("hasRole('USER')")
    @Operation(tags = "Device Service", description = "Update device configuration.")
    ResponseEntity<DeviceDto> updateDeviceConfig(@RequestBody DeviceDto deviceDto) {
        DeviceDto result = deviceService.updateDevice(deviceDto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sync/health-data")
    @PreAuthorize("hasRole('USER')")
    @Operation(tags = "Device Service", description = "Sync health data from device.")
    ResponseEntity syncHealthData(@RequestBody @Valid HealthDataDto healthDataDto) {
        boolean result = deviceService.syncHealthData(healthDataDto);
        if (!result) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.status(202).build();
    }
}
