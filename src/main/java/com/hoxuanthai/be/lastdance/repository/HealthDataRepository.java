package com.hoxuanthai.be.lastdance.repository;

import com.hoxuanthai.be.lastdance.model.Device;
import com.hoxuanthai.be.lastdance.model.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HealthDataRepository extends JpaRepository<HealthData, Long> {
    List<HealthData> findByDeviceAndTimestampBetween(Device device, LocalDateTime startDate, LocalDateTime endDate);
}
