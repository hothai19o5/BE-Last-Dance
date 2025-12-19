package com.hoxuanthai.be.lastdance.service;

import com.hoxuanthai.be.lastdance.dto.DataPoint;
import com.hoxuanthai.be.lastdance.dto.DeviceDto;
import com.hoxuanthai.be.lastdance.dto.HealthDataDto;
import com.hoxuanthai.be.lastdance.dto.response.DevicesStats;
import com.hoxuanthai.be.lastdance.exceptions.ResourceNotFoundException;
import com.hoxuanthai.be.lastdance.mapper.DeviceMapper;
import com.hoxuanthai.be.lastdance.entity.Device;
import com.hoxuanthai.be.lastdance.entity.HealthData;
import com.hoxuanthai.be.lastdance.entity.User;
import com.hoxuanthai.be.lastdance.repository.DeviceRepository;
import com.hoxuanthai.be.lastdance.repository.HealthDataRepository;
import com.hoxuanthai.be.lastdance.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;

    private final UserRepository userRepository;

    private final HealthDataRepository healthDataRepository;

    private final DeviceMapper deviceMapper;

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
     * @param deviceDto Đối tượng DeviceDto chứa thông tin cập nhật. ID của thiết bị là bắt buộc.
     * @return DeviceDto của thiết bị sau khi đã được cập nhật.
     * @throws RuntimeException nếu ID của thiết bị không được cung cấp hoặc không tìm thấy thiết bị tương ứng.
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
     * Đồng bộ hóa dữ liệu sức khỏe từ một thiết bị.
     * Phương thức này nhận dữ liệu sức khỏe, tìm thiết bị tương ứng bằng UUID,
     * và lưu các điểm dữ liệu sức khỏe vào cơ sở dữ liệu.
     *
     * @param healthDataDto Đối tượng chứa UUID của thiết bị và danh sách các điểm dữ liệu sức khỏe.
     * @return true nếu đồng bộ thành công, false nếu không tìm thấy thiết bị.
     */
    @Override
    @Transactional
    public void syncHealthData(HealthDataDto healthDataDto) {

        Device device = deviceRepository.findByDeviceUuid(healthDataDto.getDeviceUuid()).orElseThrow(() -> new ResourceNotFoundException("Device with UUID " + healthDataDto.getDeviceUuid() + " not found"));

        List<HealthData> healthDataList = new ArrayList<>();
        for (DataPoint datapoint : healthDataDto.getDataPoints()) {
            HealthData healthData = HealthData.builder()
                    .device(device)
                    .timestamp(datapoint.getTimestamp())
                    .heartRate(datapoint.getHeartRate())
                    .stepsCount(datapoint.getStepCount())
                    .spo2Percent(datapoint.getSpo2())
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
     * @return HealthDataDto chứa danh sách các điểm dữ liệu sức khỏe trong khoảng thời gian.
     * @throws RuntimeException nếu không tìm thấy người dùng hoặc thiết bị tương ứng.
     */
    @Override
    public HealthDataDto getHealthData(Long userId, String deviceUuid, LocalDateTime startDate, LocalDateTime endDate) {
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(""));
        Device device = deviceRepository.findByDeviceUuid(deviceUuid).orElseThrow(() -> new ResourceNotFoundException(""));
        List<HealthData> healthDataList = healthDataRepository.findByDeviceAndTimestampBetween(device, startDate, endDate);
        List<DataPoint> dataPoints = new ArrayList<>();
        for (HealthData healthData : healthDataList) {
            DataPoint dataPoint = DataPoint.builder()
                    .timestamp(healthData.getTimestamp())
                    .heartRate(healthData.getHeartRate())
                    .stepCount(healthData.getStepsCount())
                    .spo2(healthData.getSpo2Percent())
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
        return deviceRepository.findAll(pageable).map(deviceMapper::toDto);
    }

    /**
     * Lấy thống kê về thiết bị trong hệ thống.
     *
     * @return DevicesStats chứa tổng số thiết bị, số thiết bị hoạt động và số thiết bị không hoạt động.
     */
    @Override
    public DevicesStats getDevicesStats() {
        Long totalDevices = deviceRepository.count();
        Long activeDevices = deviceRepository.countActiveDevices();
        Long inactiveDevices = totalDevices - activeDevices;

        return DevicesStats.builder()
                .totalDevices(totalDevices)
                .activeDevices(activeDevices)
                .inactiveDevices(inactiveDevices)
                .build();
    }
}
