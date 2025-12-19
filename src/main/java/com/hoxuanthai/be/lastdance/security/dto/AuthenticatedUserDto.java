package com.hoxuanthai.be.lastdance.security.dto;

import com.hoxuanthai.be.lastdance.entity.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticatedUserDto {

	private Long id;

	private String firstName;

	private String lastName;

	private String username;

	private String password;

	private UserRole userRole;

	private boolean enabled;
}
