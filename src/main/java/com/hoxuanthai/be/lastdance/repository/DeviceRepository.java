package com.hoxuanthai.be.lastdance.repository;

import com.hoxuanthai.be.lastdance.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    @Query("SELECT d FROM Device d JOIN d.user u WHERE u.id = ?1")
    List<Device> findAllByUserId(Long userId);

    Optional<Device> findByDeviceUuid(String deviceUuid);

    @Query("SELECT COUNT(d.id) FROM Device d WHERE d.isActive = true")
    Long countActiveDevices();
}
