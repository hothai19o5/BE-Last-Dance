package com.hoxuanthai.be.lastdance.security.mapper;

import com.hoxuanthai.be.lastdance.dto.DeviceDto;
import com.hoxuanthai.be.lastdance.dto.UserDto;
import com.hoxuanthai.be.lastdance.entity.Device;
import com.hoxuanthai.be.lastdance.entity.User;
import com.hoxuanthai.be.lastdance.mapper.DeviceMapper;
import com.hoxuanthai.be.lastdance.security.dto.AuthenticatedUserDto;
import com.hoxuanthai.be.lastdance.security.dto.RegistrationRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {

	DeviceMapper deviceMapper  = Mappers.getMapper(DeviceMapper.class);

	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

	User convertToUser(RegistrationRequest registrationRequest);

	AuthenticatedUserDto convertToAuthenticatedUserDto(User user);

	User convertToUser(AuthenticatedUserDto authenticatedUserDto);

	@Mapping(target = "email")
	@Mapping(target = "devices", ignore = true)
	@Mapping(target = "deviceIds", source = "devices", qualifiedByName = "mapDeviceIds")
	UserDto toDto(User user);

	@Mapping(target = "deviceIds", ignore = true)
	@Mapping(target = "devices", source = "devices", qualifiedByName = "mapDevices")
	UserDto toDetailDto(User user);

	@Named("mapDeviceIds")
	default List<Long> mapDeviceIds(List<Device> devices) {
		if (devices == null) {
			return null;
		}
		return devices.stream()
				.map(Device::getId)
				.collect(Collectors.toList());
	}

	@Named("mapDevices")
	default List<DeviceDto> mapDevices(List<Device> devices) {
		return devices.stream().map(device -> deviceMapper.toDto(device)).collect(Collectors.toList());
	}

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "devices", ignore = true)
	@Mapping(target = "username", ignore = true)
	@Mapping(target = "email", ignore = true)
	@Mapping(target = "firstName", ignore = true)
	@Mapping(target = "lastName", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "userRole", ignore = true)
	@Mapping(target = "enabled", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	User updateEntity(UserDto userDto, @MappingTarget User user);
}
