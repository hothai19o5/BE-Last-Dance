package com.hoxuanthai.be.lastdance.security.service;

import com.hoxuanthai.be.lastdance.dto.UserDto;
import com.hoxuanthai.be.lastdance.dto.response.UsersStats;
import com.hoxuanthai.be.lastdance.entity.User;
import com.hoxuanthai.be.lastdance.security.dto.AuthenticatedUserDto;
import com.hoxuanthai.be.lastdance.security.dto.RegistrationRequest;
import com.hoxuanthai.be.lastdance.security.dto.RegistrationResponse;
import org.springframework.data.domain.Page;

public interface UserService {

	User findByUsername(String username);

	RegistrationResponse registration(RegistrationRequest registrationRequest);

	AuthenticatedUserDto findAuthenticatedUserByUsername(String username);

	Page<UserDto> getAllUsers(int page, int size, String sortBy);

	UserDto getUserById(Long userId);

	UserDto getUserDetailByUsername(String username);

	UserDto updateUserInfo(UserDto userDto);

	void changePassword(Long userId, String oldPassword, String newPassword);

	UsersStats getUsersStats();

	void deleteUserById(Long id);

	String getPresignedUrlForAvatarUpload(Long userId, String fileName);
}
