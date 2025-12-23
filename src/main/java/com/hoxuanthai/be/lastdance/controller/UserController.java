package com.hoxuanthai.be.lastdance.controller;

import com.hoxuanthai.be.lastdance.dto.DeviceDto;
import com.hoxuanthai.be.lastdance.dto.HealthDataDto;
import com.hoxuanthai.be.lastdance.dto.response.PageResponse;
import com.hoxuanthai.be.lastdance.dto.UserDto;
import com.hoxuanthai.be.lastdance.security.service.UserService;
import com.hoxuanthai.be.lastdance.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final DeviceService deviceService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(tags = "User Service", description = "Get all users ROLE_USER in the system.")
    public ResponseEntity<BaseResponse<PageResponse<UserDto>>> getAllUsers(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy
    ) {
        Page<UserDto> usersPage = userService.getAllUsers(page, size, sortBy);

        PageResponse<UserDto> pageResponse = PageResponse.<UserDto>builder()
                .content(usersPage.getContent())
                .page(usersPage.getNumber())
                .size(usersPage.getSize())
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .first(usersPage.isFirst())
                .last(usersPage.isLast())
                .build();

        return BaseResponse.success(pageResponse);
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(tags = "User Service", description = "Get user detail by id.")
    public ResponseEntity<BaseResponse<UserDto>> getUserById(@PathVariable Long id) {
        UserDto result = userService.getUserById(id);
        return BaseResponse.success(result);
    }

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(tags = "User Service", description = "Get profile of the logged-in user.")
    public ResponseEntity<BaseResponse<UserDto>> getProfile(@RequestParam String username) {
        return BaseResponse.success(userService.getUserDetailByUsername(username));
    }
    

    @GetMapping("/user/{id}/devices")
    @Operation(tags = "User Service", description = "Get all devices registered to the user.")
    ResponseEntity<BaseResponse<List<DeviceDto>>> getAllDevices(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        List<DeviceDto> result = deviceService.getAllDevicesByUserId(id);
        return BaseResponse.success(result);
    }

    @PatchMapping("/user")
    @PreAuthorize("hasRole('USER')")
    @Operation(tags = "User Service", description = "Update user information.")
    public ResponseEntity<BaseResponse<UserDto>> updateUserInfo(
            @RequestBody UserDto userDto
    ) {
        UserDto result = userService.updateUserInfo(userDto);
        return BaseResponse.success(result);
    }

    @PatchMapping("/user/{id}/password")
    @Operation(tags = "User Service", description = "User change password.")
    public ResponseEntity<BaseResponse<String>> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @PathVariable Long id
    ) {
        userService.changePassword(id, oldPassword, newPassword);
        return BaseResponse.success(null, "Password updated successfully.");
    }

    @GetMapping("/user/{userId}/{deviceUuid}/health-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(tags = "Dashboard Service", description = "Get user health data for a specific date range.")
    ResponseEntity<BaseResponse<HealthDataDto>> getUserHealthData(
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) LocalDateTime endDate,
            @PathVariable Long userId,
            @PathVariable String deviceUuid
    ) {
        if (endDate != null) {
            endDate = startDate;
        }
        HealthDataDto result = deviceService.getHealthData(userId, deviceUuid, startDate, endDate);
        return BaseResponse.success(result);
    }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(tags = "User Service", description = "Admin delete user by id.")
    public ResponseEntity<BaseResponse<String>> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return BaseResponse.success(null, "User deleted successfully.");
    }
}
