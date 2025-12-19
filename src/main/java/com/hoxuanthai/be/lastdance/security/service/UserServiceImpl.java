package com.hoxuanthai.be.lastdance.security.service;

import com.hoxuanthai.be.lastdance.dto.UserDto;
import com.hoxuanthai.be.lastdance.dto.response.UsersStats;
import com.hoxuanthai.be.lastdance.entity.User;
import com.hoxuanthai.be.lastdance.entity.UserRole;
import com.hoxuanthai.be.lastdance.exceptions.ResourceNotFoundException;
import com.hoxuanthai.be.lastdance.repository.UserRepository;
import com.hoxuanthai.be.lastdance.security.dto.AuthenticatedUserDto;
import com.hoxuanthai.be.lastdance.security.dto.RegistrationRequest;
import com.hoxuanthai.be.lastdance.security.dto.RegistrationResponse;
import com.hoxuanthai.be.lastdance.security.mapper.UserMapper;
import com.hoxuanthai.be.lastdance.service.UserValidationService;
import com.hoxuanthai.be.lastdance.utils.GeneralMessageAccessor;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private static final String REGISTRATION_SUCCESSFUL = "registration_successful";

	private final UserRepository userRepository;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	private final UserValidationService userValidationService;

	private final GeneralMessageAccessor generalMessageAccessor;

	private final UserMapper userMapper;

	@Override
	public User findByUsername(String username) {

		return userRepository.findByUsername(username);
	}


	/**
	 * Registers a new user in the system.

	 * @param registrationRequest the registration request containing user details
	 * @return RegistrationResponse containing success message
	 */
	@Override
	@Transactional
	public RegistrationResponse registration(RegistrationRequest registrationRequest) {

		userValidationService.validateUser(registrationRequest);

		final User user = UserMapper.INSTANCE.convertToUser(registrationRequest);
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setUserRole(UserRole.USER);

		userRepository.save(user);

		final String username = registrationRequest.getUsername();
		final String registrationSuccessMessage = generalMessageAccessor.getMessage(null, REGISTRATION_SUCCESSFUL, username);

		log.info("{} registered successfully!", username);

		return new RegistrationResponse(registrationSuccessMessage);
	}

	/**
	 * Finds an authenticated user by their username.

	 * @param username the username of the user to find
	 * @return AuthenticatedUserDto containing user details
	 * @throws RuntimeException if no user is found with the given username
	 */
	@Override
	public AuthenticatedUserDto findAuthenticatedUserByUsername(String username) {

		final User user = findByUsername(username);

		return UserMapper.INSTANCE.convertToAuthenticatedUserDto(user);
	}

	/**
	 * Retrieves a paginated list of all users along with their associated devices.

	 * @param page   the page number to retrieve
	 * @param size   the number of users per page
	 * @param sortBy the field to sort the users by
	 * @return Page of UserDto containing user details and associated devices
	 */
	@Override
	public Page<UserDto> getAllUsers(int page, int size, String sortBy) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
		return userRepository.findAllWithDevices(pageable).map(user -> userMapper.toDto(user));
	}

	/**
	 * Retrieves a user by their ID along with associated devices.

	 * @param userId the ID of the user to retrieve
	 * @return UserDto containing user details and associated devices
	 * @throws RuntimeException if no user is found with the given ID
	 */
	@Override
	public UserDto getUserById(Long userId) {
		User user = userRepository.findByIdWithDevices(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with Id: " + userId));
		return userMapper.toDetailDto(user);
	}

	/**
	 * Cập nhật thông tin người dùng ( weight, height, age, dob, ... )
	 * @param userDto thông tin người dùng cần cập nhật
	 * @return UserDto thông tin người dùng sau khi cập nhật
	 * @throws ResourceNotFoundException nếu không tìm thấy người dùng với Id đã cho
	 */
	@Override
	@Transactional
	public UserDto updateUserInfo(UserDto userDto) {
		User existingUser = userRepository.findById(userDto.getId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found with Id: " + userDto.getId()));
		User updatedUser = userMapper.updateEntity(userDto, existingUser);
		User savedUser = userRepository.save(updatedUser);
		return userMapper.toDto(savedUser);
	}

	/**
	 * Đổi mật khẩu cho người dùng.
	 * @param userId Id của người dùng
	 * @param oldPassword mật khẩu cũ
	 * @param newPassword mật khẩu mới
	 * @throws ResourceNotFoundException nếu không tìm thấy người dùng với Id đã cho
	 */
	@Override
	@Transactional
	public void changePassword(Long userId, String oldPassword, String newPassword) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with Id: " + userId));

		if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
			log.warn("Old password does not match for user with Id: {}", userId);
			return;
		}

		user.setPassword(bCryptPasswordEncoder.encode(newPassword));
		userRepository.save(user);
		log.info("Password changed successfully for user with Id: {}", userId);
	}

	/**
	 * Lấy thống kê về người dùng trong hệ thống.
	 * @return UsersStats đối tượng chứa thống kê về người dùng
	 */
	@Override
	public UsersStats getUsersStats() {
		Long totalUsers = userRepository.countUser();
		Long totalUsersActive = userRepository.countUserActive();
		return UsersStats.builder()
				.totalUsers(totalUsers)
				.activeUsers(totalUsersActive)
				.build();
	}
}
