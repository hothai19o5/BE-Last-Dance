package com.hoxuanthai.be.lastdance.controller;

import com.hoxuanthai.be.lastdance.dto.response.DashboardStats;
import com.hoxuanthai.be.lastdance.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(tags = "Dashboard Service", description = "Get dashboard overview data.")
    ResponseEntity<BaseResponse<DashboardStats>> getDashboardOverview() {
        return BaseResponse.success(dashboardService.getDashboardStats());
    }
}
