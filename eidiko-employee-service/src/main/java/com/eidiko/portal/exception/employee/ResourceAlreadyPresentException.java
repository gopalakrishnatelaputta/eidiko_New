package com.eidiko.portal.exception.employee;

public class ResourceAlreadyPresentException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResourceAlreadyPresentException() {
		super();
	}

	public ResourceAlreadyPresentException(String message) {
		super(message);
	}

}
