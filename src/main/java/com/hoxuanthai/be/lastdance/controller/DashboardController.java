package com.hoxuanthai.be.lastdance.controller;

import com.hoxuanthai.be.lastdance.dto.HealthDataDto;
import com.hoxuanthai.be.lastdance.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class DashboardController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/user/{userId}/{deviceUuid}/health-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(tags = "Dashboard Service", description = "Get user health data for a specific date range.")
    ResponseEntity<HealthDataDto> getUserHealthData(
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) LocalDateTime endDate,
            @PathVariable Long userId,
            @PathVariable String deviceUuid
    ) {
        if (endDate != null) {
            endDate = startDate;
        }
        HealthDataDto result = deviceService.getHealthData(userId, deviceUuid, startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/{userId}/summary")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(tags = "Dashboard Service", description = "Get user summary for a specific date.")
    ResponseEntity getUserSummary(@RequestParam("date") String date) {
        return ResponseEntity.ok().build();
    }
}
