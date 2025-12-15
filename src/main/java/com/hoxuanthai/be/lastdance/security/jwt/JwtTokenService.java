package com.hoxuanthai.be.lastdance.security.jwt;

import com.hoxuanthai.be.lastdance.security.mapper.UserMapper;
import com.hoxuanthai.be.lastdance.security.service.UserService;
import com.hoxuanthai.be.lastdance.entity.User;
import com.hoxuanthai.be.lastdance.security.dto.AuthenticatedUserDto;
import com.hoxuanthai.be.lastdance.security.dto.LoginRequest;
import com.hoxuanthai.be.lastdance.security.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Created on November 2025
 *
 * @author HoXuanThai
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {

	private final UserService userService;

	private final JwtTokenManager jwtTokenManager;

	private final AuthenticationManager authenticationManager;

	public LoginResponse getLoginResponse(LoginRequest loginRequest) {

		final String username = loginRequest.getUsername();
		final String password = loginRequest.getPassword();

		final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);

		authenticationManager.authenticate(usernamePasswordAuthenticationToken);

		final AuthenticatedUserDto authenticatedUserDto = userService.findAuthenticatedUserByUsername(username);

		final User user = UserMapper.INSTANCE.convertToUser(authenticatedUserDto);
		final String token = jwtTokenManager.generateToken(user);

		log.info("{} has successfully logged in!", user.getUsername());

		return new LoginResponse(token);
	}

}
