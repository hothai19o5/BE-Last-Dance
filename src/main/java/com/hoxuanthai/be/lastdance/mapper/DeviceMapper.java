package com.hoxuanthai.be.lastdance.mapper;

import com.hoxuanthai.be.lastdance.dto.DeviceDto;
import com.hoxuanthai.be.lastdance.model.Device;
import org.mapstruct.*;

/**
 * Created on November 2025
 *
 * @author HoXuanThai
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface DeviceMapper {

    @Mapping(target="userId", source="user.id")
    DeviceDto toDto(Device device);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastSyncAt", ignore = true)
    Device toEntity(DeviceDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastSyncAt", ignore = true)
    Device updateEntity(DeviceDto dto, @MappingTarget Device entity);
}
