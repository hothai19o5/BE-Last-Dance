package com.hoxuanthai.be.lastdance.controller;

import com.hoxuanthai.be.lastdance.security.dto.RegistrationRequest;
import com.hoxuanthai.be.lastdance.security.dto.RegistrationResponse;
import com.hoxuanthai.be.lastdance.security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegistrationController {

	private final UserService userService;

	@PostMapping
	@Operation(tags = "Register Service", description = "You can register to the system by sending information in the appropriate format.")
	public ResponseEntity<BaseResponse<RegistrationResponse>> registrationRequest(@Valid @RequestBody RegistrationRequest registrationRequest) {

		final RegistrationResponse registrationResponse = userService.registration(registrationRequest);

		return BaseResponse.created(registrationResponse);
	}

}
