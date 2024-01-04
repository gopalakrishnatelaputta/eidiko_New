package com.eidiko.portal.config.employee;


import com.eidiko.portal.entities.employee.Employee;
import com.eidiko.portal.helper.employee.ConstantValues;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.security.sasl.AuthenticationException;

public class SecurityUtil {
	
	private SecurityUtil() {}

	public static Employee getCurrentUserDetails() throws AuthenticationException {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();
			if(principal != null) {
				Employee employee = (Employee) principal;
				return employee;
			}
			
		}
		throw new AuthenticationException(ConstantValues.SESSION_HAS_BEEN_EXPIRED);
	}
	
	
	public static boolean isAuthenticatedUser() throws AuthenticationException {
		return getCurrentUserDetails() != null;
	}
	
	
	
}
