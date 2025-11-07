package com.hoxuanthai.be.lastdance.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created on November 2025
 *
 * @author HoXuanThai
 */
@Getter
@RequiredArgsConstructor
public class RegistrationException extends RuntimeException {

	private final String errorMessage;

}
