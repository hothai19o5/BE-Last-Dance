package com.hoxuanthai.be.lastdance.security.dto;

import com.hoxuanthai.be.lastdance.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RegistrationRequest {

	@NotEmpty(message = "registration_first_name_not_empty")
	private String firstName;

	@NotEmpty(message = "registration_last_name_not_empty")
	private String lastName;

	@Email(message = "registration_email_is_not_valid")
	@NotEmpty(message = "registration_email_not_empty")
	private String email;

	@NotEmpty(message = "registration_username_not_empty")
	private String username;

	@NotEmpty(message = "registration_password_not_empty")
	private String password;

	private LocalDate dob;

	private Gender gender;
}
