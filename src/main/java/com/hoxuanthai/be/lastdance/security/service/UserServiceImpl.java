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
import com.hoxuanthai.be.lastdance.service.S3StorageService;
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

	private final S3StorageService s3StorageService;

	@Override
	public User findByUsername(String username) {

		return userRepository.findByUsername(username);
	}

	/**
	 * Đăng ký người dùng mới.
	 * 
	 * @param registrationRequest yêu cầu đăng ký người dùng
	 * @return RegistrationResponse phản hồi đăng ký người dùng
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
		final String registrationSuccessMessage = generalMessageAccessor.getMessage(null, REGISTRATION_SUCCESSFUL,
				username);

		log.info("{} registered successfully!", username);

		return new RegistrationResponse(registrationSuccessMessage);
	}

	/**
	 * Tìm người dùng đã xác thực theo tên người dùng.
	 * 
	 * @param username tên người dùng
	 * @return AuthenticatedUserDto chứa thông tin người dùng đã xác thực
	 * @throws RuntimeException nếu không tìm thấy người dùng với tên đã cho
	 */
	@Override
	public AuthenticatedUserDto findAuthenticatedUserByUsername(String username) {

		final User user = findByUsername(username);

		return UserMapper.INSTANCE.convertToAuthenticatedUserDto(user);
	}

	/**
	 * Lấy tất cả người dùng với phân trang và sắp xếp, bao gồm cả thiết bị liên
	 * 
	 * @param page   số trang hiện tại
	 * @param size   kích thước trang
	 * @param sortBy trường để sắp xếp
	 * @return Page<UserDto> trang chứa danh sách UserDto
	 */
	@Override
	public Page<UserDto> getAllUsers(int page, int size, String sortBy) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
		return userRepository.findAllWithDevices(pageable).map(user -> userMapper.toDto(user));
	}

	/**
	 * Lấy thông tin chi tiết người dùng theo ID, bao gồm cả thiết bị liên
	 * 
	 * @param userId ID của người dùng
	 * @return UserDto thông tin chi tiết của người dùng
	 * @throws RuntimeException nếu không tìm thấy người dùng với Id đã cho
	 */
	@Override
	public UserDto getUserById(Long userId) {
		User user = userRepository.findByIdWithDevices(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with Id: " + userId));
		return userMapper.toDetailDto(user);
	}

	/**
	 * Lấy thông tin chi tiết bản thân.
	 * 
	 * @return Detail User Dto chứa thông tin chi tiết của người dùng
	 * @throws ResourceNotFoundException nếu không tìm thấy người dùng với username
	 *                                   đã cho
	 */
	@Override
	public UserDto getUserDetailByUsername(String username) {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new ResourceNotFoundException("User not found with username: " + username);
		}
		return userMapper.toDetailDto(user);
	}

	/**
	 * Cập nhật thông tin người dùng ( weight, height, age, dob, ... )
	 * 
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
	 * 
	 * @param userId      Id của người dùng
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
	 * 
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

	/**
	 * Xóa người dùng bằng cách đánh dấu là đã xóa (soft delete).
	 * 
	 * @param id Id của người dùng cần xóa
	 * @throws ResourceNotFoundException nếu không tìm thấy người dùng với Id đã cho
	 */
	@Override
	@Transactional
	public void deleteUserById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with Id: " + id));

		user.setDeleted(true);
		userRepository.save(user);
	}

	/**
	 * Lấy URL được ký trước để tải lên avatar người dùng.
	 * 
	 * @param userId   Id của người dùng
	 * @param fileName Tên tệp avatar
	 * @return URL được ký trước để tải lên avatar
	 */
	@Override
	public String getPresignedUrlForAvatarUpload(Long userId, String fileName) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with Id: " + userId));
		String key = "avatars/" + user.getUsername() + "_" + System.currentTimeMillis() + "_" + fileName;
		String presignedUrl = s3StorageService.generatePresignedUploadUrl(key);
		return presignedUrl;
	}

	/**
	 * Enable a user account.
	 * 
	 * @param userId Id của người dùng cần enable
	 * @throws ResourceNotFoundException nếu không tìm thấy người dùng với Id đã cho
	 */
	@Override
	@Transactional
	public void enableUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with Id: " + userId));
		user.setEnabled(true);
		userRepository.save(user);
		log.info("User with Id: {} has been enabled", userId);
	}

	/**
	 * Disable a user account.
	 * 
	 * @param userId Id của người dùng cần disable
	 * @throws ResourceNotFoundException nếu không tìm thấy người dùng với Id đã cho
	 */
	@Override
	@Transactional
	public void disableUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with Id: " + userId));
		user.setEnabled(false);
		userRepository.save(user);
		log.info("User with Id: {} has been disabled", userId);
	}
}
