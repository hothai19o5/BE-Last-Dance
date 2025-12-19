package com.hoxuanthai.be.lastdance.controller;

import com.hoxuanthai.be.lastdance.dto.HealthDataDto;
import com.hoxuanthai.be.lastdance.service.DashboardService;
import com.hoxuanthai.be.lastdance.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DeviceService deviceService;

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(tags = "Dashboard Service", description = "Get dashboard overview data.")
    ResponseEntity getDashboardOverview() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }
}
