package com.hoxuanthai.be.lastdance.security.dto;

import com.hoxuanthai.be.lastdance.entity.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created on November 2025
 *
 * @author HoXuanThai
 */
@Getter
@Setter
@NoArgsConstructor
public class AuthenticatedUserDto {

	private String name;

	private String username;

	private String password;

	private UserRole userRole;

}
