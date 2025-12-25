package com.hoxuanthai.be.lastdance.service;

import com.hoxuanthai.be.lastdance.dto.response.*;
import com.hoxuanthai.be.lastdance.security.service.UserService;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final MeterRegistry meterRegistry;
    private final UserService userService;
    private final DeviceService deviceService;
    private final DataSource dataSource;

    /**
     * Lấy thống kê tổng quan của hệ thống.
     *
     * @return DashboardStats chứa các thống kê về người dùng, thiết bị, máy chủ, cơ sở dữ liệu và sử dụng API.
     */
    @Override
    public DashboardStats getDashboardStats() {
        UsersStats usersStats = getUsersStats();
        DevicesStats devicesStats = getDevicesStats();
        ServerStats serverStats = getServerStats();
        DatabaseStats databaseStats = getDatabaseStats();
        ApiUsageStats apiUsageStats = getApiUsageStats();

        return DashboardStats.builder()
                .usersStats(usersStats)
                .devicesStats(devicesStats)
                .serverStats(serverStats)
                .databaseStats(databaseStats)
                .apiUsageStats(apiUsageStats)
                .build();
    }

    /**
     * Lấy thống kê về người dùng.
     *
     * @return UsersStats chứa các thống kê về người dùng (tổng số người dùng, người dùng hoạt động, người dùng mới trong 1 ngày qua).
     */
    @Override
    public UsersStats getUsersStats() {
        return userService.getUsersStats();
    }

    /**
     * Lấy thống kê về thiết bị.
     *
     * @return DevicesStats chứa các thống kê về thiết bị (tổng số thiết bị, thiết bị hoạt động, thiết bị không hoạt động).
     */
    @Override
    public DevicesStats getDevicesStats() {
        return deviceService.getDevicesStats();
    }

    /**
     * Lấy thống kê về máy chủ.
     *
     * @return ServerStats chứa các thống kê về máy chủ (thời gian hoạt động, sử dụng CPU, sử dụng bộ nhớ, sử dụng đĩa).
     */
    @Override
    public ServerStats getServerStats() {
        // CPU usage
        double cpuUsage = getGaugeValue("system.cpu.usage");
        // Memory usage
        double memoryUsage = getGaugeValue("jvm.memory.used");
        double memoryMax = getGaugeValue("jvm.memory.max");
        // Disk usage
        double diskFree = getGaugeValue("disk.free");
        double diskTotal = getGaugeValue("disk.total");
        double diskUsage = (diskTotal > 0) ? (diskTotal - diskFree) / diskTotal : 0.0;
        // Uptime
        double uptimeSeconds = getGaugeValue("process.uptime");

        return ServerStats.builder()
                .uptime(uptimeSeconds)
                .cpuUsage(cpuUsage * 100.0)
                .memoryUsage(memoryMax > 0 ? (memoryUsage / memoryMax) * 100.0 : 0.0)
                .diskUsage(diskUsage * 100.0)
                .totalMemoryGB(Math.round(memoryMax / (1024 * 1024 * 1024) * 100.0) / 100.0)
                .totalDiskGB(Math.round(diskTotal / (1024 * 1024 * 1024) * 100.0) / 100.0)
                .build();
    }

    // Hàm helper để lấy giá trị an toàn, tránh Exception
    private double getGaugeValue(String metricName) {
        var meter = meterRegistry.find(metricName).gauge();
        return (meter != null) ? meter.value() : 0.0;
    }

    @Override
    public DatabaseStats getDatabaseStats() {
        boolean isConnected = false;
        Double responseTimeMs = null;
        Long connectionPoolSize = null;
        Long activeConnections = null;
        Double databaseSizeGB = null;

        long startTime = System.currentTimeMillis();

        try (Connection connection = dataSource.getConnection()) {
            isConnected = connection.isValid(5);
            responseTimeMs = (double) (System.currentTimeMillis() - startTime);

            if (dataSource instanceof HikariDataSource hikariDataSource) {
                HikariPoolMXBean poolMXBean = hikariDataSource.getHikariPoolMXBean();
                if (poolMXBean != null) {
                    connectionPoolSize = (long) hikariDataSource.getMaximumPoolSize();
                    activeConnections = (long) poolMXBean.getActiveConnections();
                }
            }

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT pg_database_size(current_database())")) {
                if (rs.next()) {
                    long sizeBytes = rs.getLong(1);
                    databaseSizeGB = Math.round((double) sizeBytes / (1024 * 1024 * 1024) * 1000.0) / 1000.0;
                }
            }
        } catch (Exception e) {
            log.error("Error getting database stats: {}", e.getMessage());
        }

        return DatabaseStats.builder()
                .isConnected(isConnected)
                .responseTimeMs(responseTimeMs)
                .connectionPoolSize(connectionPoolSize)
                .activeConnections(activeConnections)
                .databaseSizeGB(databaseSizeGB)
                .lastBackupTime(null)
                .build();
    }

    @Override
    public ApiUsageStats getApiUsageStats() {

        var timer = meterRegistry.find("http.server.requests").timer();

        if (timer == null) {
            return ApiUsageStats.builder()
                    .requestsPerMinute(0L)
                    .averageResponseTimeMs(0.0)
                    .successRatePercentage(100.0)
                    .build();
        }

        return ApiUsageStats.builder()
                .requestsPerMinute(Math.round(timer.count() / (timer.totalTime(TimeUnit.MINUTES) > 0 ? timer.totalTime(TimeUnit.MINUTES) : 1)))
                .averageResponseTimeMs(timer.mean(TimeUnit.MILLISECONDS))
                .successRatePercentage(calculateSuccessRate())
                .build();
    }

    // Helper method to calculate success rate
    private Double calculateSuccessRate() {
        // Lấy tổng số request (tất cả các status code)
        var allRequests = meterRegistry.find("http.server.requests").timers();
        long totalCount = allRequests.stream().mapToLong(Timer::count).sum();

        if (totalCount == 0) return 100.0;

        // Lấy số request thành công (thường là tag outcome="SUCCESS")
        var successRequests = meterRegistry.find("http.server.requests")
                .tag("outcome", "SUCCESS")
                .timers();
        long successCount = successRequests.stream().mapToLong(Timer::count).sum();

        // Tính tỷ lệ phần trăm
        return (double) successCount / totalCount * 100;
    }
}
