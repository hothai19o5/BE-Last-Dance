package com.hoxuanthai.be.lastdance.repository;

import com.hoxuanthai.be.lastdance.entity.Device;
import com.hoxuanthai.be.lastdance.entity.HealthData;
import com.hoxuanthai.be.lastdance.entity.HealthDataId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HealthDataRepository extends JpaRepository<HealthData, HealthDataId> {
    List<HealthData> findByDeviceAndTimestampBetween(Device device, LocalDateTime startDate, LocalDateTime endDate);
}
