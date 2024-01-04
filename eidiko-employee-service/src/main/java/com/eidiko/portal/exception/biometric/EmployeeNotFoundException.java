package com.eidiko.portal.exception.biometric;

public class EmployeeNotFoundException extends RuntimeException {

	 public EmployeeNotFoundException() {
	        super();
	    }

	    public EmployeeNotFoundException(String message) {
	        super(message);
	    }
}
