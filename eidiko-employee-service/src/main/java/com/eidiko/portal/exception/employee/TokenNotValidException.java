package com.eidiko.portal.exception.employee;

import org.springframework.security.core.AuthenticationException;

public class TokenNotValidException extends AuthenticationException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TokenNotValidException(String message) {
        super(message);
    }

    public TokenNotValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
