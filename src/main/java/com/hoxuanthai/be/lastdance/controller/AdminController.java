package com.hoxuanthai.be.lastdance.controller;

import com.hoxuanthai.be.lastdance.dto.UserOverviewDto;
import com.hoxuanthai.be.lastdance.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users/{userId}/overview")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(tags = "Admin Service", description = "Get aggregated user overview (privacy-compliant, no raw health data)")
    ResponseEntity<BaseResponse<UserOverviewDto>> getUserOverview(@PathVariable Long userId) {
        UserOverviewDto overview = adminService.getUserOverview(userId);
        return BaseResponse.success(overview);
    }
}
