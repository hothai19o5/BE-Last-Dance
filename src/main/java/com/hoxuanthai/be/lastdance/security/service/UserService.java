package com.hoxuanthai.be.lastdance.security.service;

import com.hoxuanthai.be.lastdance.model.User;
import com.hoxuanthai.be.lastdance.security.dto.AuthenticatedUserDto;
import com.hoxuanthai.be.lastdance.security.dto.RegistrationRequest;
import com.hoxuanthai.be.lastdance.security.dto.RegistrationResponse;

/**
 * Created on November 2025
 *
 * @author HoXuanThai
 */
public interface UserService {

	User findByUsername(String username);

	RegistrationResponse registration(RegistrationRequest registrationRequest);

	AuthenticatedUserDto findAuthenticatedUserByUsername(String username);

}
