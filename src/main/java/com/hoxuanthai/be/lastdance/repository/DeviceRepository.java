package com.hoxuanthai.be.lastdance.repository;

import com.hoxuanthai.be.lastdance.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    @Query("SELECT d FROM Device d JOIN d.user u WHERE u.id = ?1 AND d.deleted = false")
    List<Device> findAllByUserId(Long userId);

    @Query("SELECT d FROM Device d WHERE d.deviceUuid = ?1 AND d.deleted = false")
    Optional<Device> findByDeviceUuid(String deviceUuid);

    @Query("SELECT COUNT(d.id) FROM Device d WHERE d.isActive = true AND d.deleted = false")
    Long countActiveDevices();

    @Query("SELECT COUNT(d.id) FROM Device d WHERE d.user.id = ?1 AND d.deleted = false")
    Long countByUserId(Long userId);

    @Query("SELECT COUNT(d.id) FROM Device d WHERE d.user.id = ?1 AND d.isActive = true AND d.deleted = false")
    Long countActiveDevicesByUserId(Long userId);

    @Query(value = "SELECT d FROM Device d WHERE d.deleted = false", countQuery = "SELECT COUNT(d) FROM Device d WHERE d.deleted = false")
    Page<Device> findAllNotDeleted(Pageable pageable);

    @Query("SELECT COUNT(d.id) FROM Device d WHERE d.deleted = false")
    Long countNotDeleted();
}
