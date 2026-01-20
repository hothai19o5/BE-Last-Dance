package com.hoxuanthai.be.lastdance.service;

import com.hoxuanthai.be.lastdance.dto.DataPoint;
import com.hoxuanthai.be.lastdance.dto.DeviceDto;
import com.hoxuanthai.be.lastdance.dto.HealthDataDto;
import com.hoxuanthai.be.lastdance.dto.StatisticsDto;
import com.hoxuanthai.be.lastdance.dto.response.DevicesStats;
import com.hoxuanthai.be.lastdance.exceptions.ResourceNotFoundException;
import com.hoxuanthai.be.lastdance.mapper.DeviceMapper;
import com.hoxuanthai.be.lastdance.entity.Device;
import com.hoxuanthai.be.lastdance.entity.HealthData;
import com.hoxuanthai.be.lastdance.entity.User;
import com.hoxuanthai.be.lastdance.repository.DeviceRepository;
import com.hoxuanthai.be.lastdance.repository.HealthDataRepository;
import com.hoxuanthai.be.lastdance.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;

    private final UserRepository userRepository;

    private final HealthDataRepository healthDataRepository;

    private final DeviceMapper deviceMapper;

    private final EntityManager entityManager;

    /**
     * Đăng ký một thiết bị mới cho người dùng.
     *
     * @param deviceDto Đối tượng DeviceDto chứa thông tin của thiết bị cần đăng ký.
     * @return DeviceDto của thiết bị đã được lưu.
     * @throws RuntimeException nếu không tìm thấy người dùng với ID được cung cấp.
     */
    @Override
    @Transactional
    public DeviceDto registerDevice(DeviceDto deviceDto) {
        User user = userRepository.findByUsername(deviceDto.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Device device = deviceMapper.toEntity(deviceDto);
        device.setUser(user);
        return deviceMapper.toDto(deviceRepository.save(device));
    }

    /**
     * Cập nhật thông tin của một thiết bị đã tồn tại.
     *
     * @param deviceDto Đối tượng DeviceDto chứa thông tin cập nhật. ID của thiết bị
     *                  là bắt buộc.
     * @return DeviceDto của thiết bị sau khi đã được cập nhật.
     * @throws RuntimeException nếu ID của thiết bị không được cung cấp hoặc không
     *                          tìm thấy thiết bị tương ứng.
     */
    @Override
    @Transactional
    public DeviceDto updateDevice(DeviceDto deviceDto) {
        if (deviceDto.getId() == null) {
            throw new RuntimeException("Device ID is required for update");
        }
        Device existingDevice = deviceRepository.findById(deviceDto.getId())
                .orElseThrow(() -> new RuntimeException("Device not found"));
        Device updatedDevice = deviceMapper.updateEntity(deviceDto, existingDevice);
        return deviceMapper.toDto(deviceRepository.save(updatedDevice));
    }

    /**
     * Lấy danh sách tất cả các thiết bị của một người dùng.
     *
     * @param userId ID của người dùng.
     * @return Danh sách các đối tượng DeviceDto thuộc về người dùng.
     * @throws RuntimeException nếu không tìm thấy người dùng với ID được cung cấp.
     */
    @Override
    public List<DeviceDto> getAllDevicesByUserId(Long userId) {
        List<Device> devices = deviceRepository.findAllByUserId(userId);
        return devices.stream().map(deviceMapper::toDto).toList();
    }

    /**
     * Lấy danh sách tất cả các thiết bị của người dùng hiện tại (dựa trên JWT
     * token).
     *
     * @return Danh sách các đối tượng DeviceDto thuộc về người dùng hiện tại.
     * @throws RuntimeException nếu không tìm thấy người dùng.
     */
    @Override
    public List<DeviceDto> getMyDevices() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return getAllDevicesByUserId(user.getId());
    }

    /**
     * Đồng bộ hóa dữ liệu sức khỏe từ một thiết bị.
     * Phương thức này nhận dữ liệu sức khỏe, tìm thiết bị tương ứng bằng UUID,
     * và lưu các điểm dữ liệu sức khỏe vào cơ sở dữ liệu.
     *
     * @param healthDataDto Đối tượng chứa UUID của thiết bị và danh sách các điểm
     *                      dữ liệu sức khỏe.
     * @return true nếu đồng bộ thành công, false nếu không tìm thấy thiết bị.
     */
    @Override
    @Transactional
    public void syncHealthData(HealthDataDto healthDataDto) {

        Device device = deviceRepository.findByDeviceUuid(healthDataDto.getDeviceUuid())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Device with UUID " + healthDataDto.getDeviceUuid() + " not found"));

        List<HealthData> healthDataList = new ArrayList<>();
        for (DataPoint datapoint : healthDataDto.getDataPoints()) {
            HealthData healthData = HealthData.builder()
                    .id(UUID.randomUUID()) // Generate a new UUID for each health data entry
                    .device(device)
                    .timestamp(datapoint.getTimestamp())
                    .heartRate(datapoint.getHeartRate())
                    .stepsCount(datapoint.getStepCount())
                    .spo2Percent(datapoint.getSpo2())
                    .caloriesBurned(datapoint.getCaloriesBurned())
                    .waterIntakeMl(datapoint.getWaterIntakeMl())
                    .activityStatus(datapoint.getActivityStatus())
                    .sleepDurationMinutes(datapoint.getSleepDurationMinutes())
                    .build();
            healthDataList.add(healthData);
        }

        healthDataRepository.saveAll(healthDataList);

    }

    /**
     * Lấy dữ liệu sức khỏe từ một thiết bị trong khoảng thời gian xác định.
     *
     * @param userId     ID của người dùng sở hữu thiết bị.
     * @param deviceUuid UUID của thiết bị.
     * @param startDate  Thời điểm bắt đầu của khoảng thời gian.
     * @param endDate    Thời điểm kết thúc của khoảng thời gian.
     * @return HealthDataDto chứa danh sách các điểm dữ liệu sức khỏe trong khoảng
     *         thời gian.
     * @throws RuntimeException nếu không tìm thấy người dùng hoặc thiết bị tương
     *                          ứng.
     */
    @Override
    public HealthDataDto getHealthData(Long userId, String deviceUuid, LocalDateTime startDate, LocalDateTime endDate) {
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(""));
        Device device = deviceRepository.findByDeviceUuid(deviceUuid)
                .orElseThrow(() -> new ResourceNotFoundException(""));
        List<HealthData> healthDataList = healthDataRepository.findByDeviceAndTimestampBetween(device, startDate,
                endDate);
        List<DataPoint> dataPoints = new ArrayList<>();
        for (HealthData healthData : healthDataList) {
            DataPoint dataPoint = DataPoint.builder()
                    .timestamp(healthData.getTimestamp())
                    .heartRate(healthData.getHeartRate())
                    .stepCount(healthData.getStepsCount())
                    .spo2(healthData.getSpo2Percent())
                    .caloriesBurned(healthData.getCaloriesBurned())
                    .waterIntakeMl(healthData.getWaterIntakeMl())
                    .activityStatus(healthData.getActivityStatus())
                    .sleepDurationMinutes(healthData.getSleepDurationMinutes())
                    .build();
            dataPoints.add(dataPoint);
        }
        return HealthDataDto.builder()
                .deviceUuid(deviceUuid)
                .dataPoints(dataPoints)
                .build();
    }

    /**
     * Lấy tất cả các thiết bị với phân trang và sắp xếp.
     *
     * @param page   Số trang (bắt đầu từ 0).
     * @param size   Kích thước trang.
     * @param sortBy Trường để sắp xếp.
     * @return Trang chứa các đối tượng DeviceDto.
     */
    @Override
    public Page<DeviceDto> getAllDevices(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return deviceRepository.findAllNotDeleted(pageable).map(deviceMapper::toDto);
    }

    /**
     * Lấy thống kê về thiết bị trong hệ thống.
     *
     * @return DevicesStats chứa tổng số thiết bị, số thiết bị hoạt động và số thiết
     *         bị không hoạt động.
     */
    @Override
    public DevicesStats getDevicesStats() {
        Long totalDevices = deviceRepository.countNotDeleted();
        Long activeDevices = deviceRepository.countActiveDevices();
        Long inactiveDevices = totalDevices - activeDevices;

        return DevicesStats.builder()
                .totalDevices(totalDevices)
                .activeDevices(activeDevices)
                .inactiveDevices(inactiveDevices)
                .build();
    }

    @Override
    @Transactional
    public void removeDevice(String deviceUuid) {
        Device device = deviceRepository.findByDeviceUuid(deviceUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Device with UUID " + deviceUuid + " not found"));
        device.setDeleted(true);
        deviceRepository.save(device);
    }

    @Override
    public StatisticsDto getHealthStatistics(String metric, String range) {
        // Get current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        // Determine time range and bucket interval
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime;
        String bucketInterval;
        DateTimeFormatter labelFormatter;

        if ("day".equalsIgnoreCase(range)) {
            startTime = endTime.minusHours(24);
            bucketInterval = "1 hour";
            labelFormatter = DateTimeFormatter.ofPattern("HH:mm");
        } else { // week
            startTime = endTime.minusDays(7);
            bucketInterval = "1 day";
            labelFormatter = DateTimeFormatter.ofPattern("MM/dd");
        }

        // Determine column name based on metric
        String columnName = getColumnNameForMetric(metric);

        // Build TimescaleDB time_bucket query
        // Note: time_bucket requires INTERVAL type, so we cast the parameter explicitly
        String sql = "SELECT " +
                "time_bucket(CAST(:interval AS INTERVAL), timestamp) AS bucket, " +
                "AVG(" + columnName + ") AS avg_value " +
                "FROM health_data hd " +
                "JOIN devices d ON hd.device_id = d.id " +
                "WHERE d.user_id = :userId " +
                "AND hd.timestamp >= :startTime " +
                "AND hd.timestamp <= :endTime " +
                "AND " + columnName + " IS NOT NULL " +
                "GROUP BY bucket " +
                "ORDER BY bucket ASC";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("interval", bucketInterval);
        query.setParameter("userId", user.getId());
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        // Process results
        List<StatisticsDto.ChartDataPoint> chartData = new ArrayList<>();
        double sum = 0;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        int count = 0;

        for (Object[] row : results) {
            LocalDateTime bucket = ((java.sql.Timestamp) row[0]).toLocalDateTime();
            Double value = ((Number) row[1]).doubleValue();

            chartData.add(StatisticsDto.ChartDataPoint.builder()
                    .value(value)
                    .label(bucket.format(labelFormatter))
                    .build());

            sum += value;
            max = Math.max(max, value);
            min = Math.min(min, value);
            count++;
        }

        double average = count > 0 ? sum / count : 0;
        double total = sum;

        // If no data, set min/max to 0
        if (count == 0) {
            min = 0;
            max = 0;
        }

        return StatisticsDto.builder()
                .chartData(chartData)
                .average(average)
                .total(total)
                .max(max)
                .min(min)
                .build();
    }

    /**
     * Map metric parameter to database column name
     */
    private String getColumnNameForMetric(String metric) {
        return switch (metric.toLowerCase()) {
            case "calories" -> "calories_burned";
            case "steps" -> "steps_count";
            case "water" -> "water_intake_ml";
            case "hr" -> "heart_rate";
            case "spo2" -> "spo2_percent";
            case "sleep" -> "sleep_duration_minutes";
            case "weight" -> "weight_kg"; // Assuming weight is stored in health_data, otherwise needs different query
            default -> throw new IllegalArgumentException("Unknown metric: " + metric);
        };
    }

    /**
     * Enable a device (set isActive to true)
     * 
     * @param deviceId ID of the device to enable
     */
    @Override
    @Transactional
    public void enableDevice(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with ID: " + deviceId));
        device.setActive(true);
        deviceRepository.save(device);
        log.info("Device with ID: {} has been enabled", deviceId);
    }

    /**
     * Disable a device (set isActive to false)
     * 
     * @param deviceId ID of the device to disable
     */
    @Override
    @Transactional
    public void disableDevice(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with ID: " + deviceId));
        device.setActive(false);
        deviceRepository.save(device);
        log.info("Device with ID: {} has been disabled", deviceId);
    }

    /**
     * Soft delete a device by ID (admin)
     * 
     * @param deviceId ID of the device to delete
     */
    @Override
    @Transactional
    public void deleteDeviceById(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with ID: " + deviceId));
        device.setDeleted(true);
        deviceRepository.save(device);
        log.info("Device with ID: {} has been soft deleted", deviceId);
    }
}
