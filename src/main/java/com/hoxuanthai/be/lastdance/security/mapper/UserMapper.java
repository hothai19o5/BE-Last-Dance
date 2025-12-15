package com.hoxuanthai.be.lastdance.security.mapper;

import com.hoxuanthai.be.lastdance.entity.User;
import com.hoxuanthai.be.lastdance.security.dto.AuthenticatedUserDto;
import com.hoxuanthai.be.lastdance.security.dto.RegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Created on November 2025
 *
 * @author HoXuanThai
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

	User convertToUser(RegistrationRequest registrationRequest);

	AuthenticatedUserDto convertToAuthenticatedUserDto(User user);

	User convertToUser(AuthenticatedUserDto authenticatedUserDto);

}
