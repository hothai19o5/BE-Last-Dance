package com.hoxuanthai.be.lastdance.controller;

import com.hoxuanthai.be.lastdance.ratelimit.KeyType;
import com.hoxuanthai.be.lastdance.ratelimit.RateLimit;
import com.hoxuanthai.be.lastdance.ratelimit.RateLimitType;
import com.hoxuanthai.be.lastdance.security.dto.LoginRequest;
import com.hoxuanthai.be.lastdance.security.dto.LoginResponse;
import com.hoxuanthai.be.lastdance.security.jwt.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

	private final JwtTokenService jwtTokenService;

	@PostMapping
	@RateLimit(type = RateLimitType.LOGIN, keyBy = KeyType.IP)
	@Operation(tags = "Login Service", description = "You must log in with the correct information to successfully obtain the token information.")
	public ResponseEntity<BaseResponse<LoginResponse>> loginRequest(@Valid @RequestBody LoginRequest loginRequest) {

		final LoginResponse loginResponse = jwtTokenService.getLoginResponse(loginRequest);

		return BaseResponse.success(loginResponse);
	}

}
