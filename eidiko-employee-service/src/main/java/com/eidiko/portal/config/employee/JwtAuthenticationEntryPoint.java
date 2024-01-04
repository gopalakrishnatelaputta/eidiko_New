package com.eidiko.portal.config.employee;

import com.eidiko.portal.helper.employee.ConstantValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {

			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			final Map<String, Object> body = new HashMap<>();
			body.put(ConstantValues.STATUS_TEXT, HttpServletResponse.SC_UNAUTHORIZED);
			body.put(ConstantValues.STATUS_CODE, HttpStatus.UNAUTHORIZED);
			body.put(ConstantValues.MESSAGE, authException.getMessage());
			body.put(ConstantValues.PATH, request.getServletPath());

			final ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(response.getOutputStream(), body);
		
		//response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied");

//		if (authException.getCause() instanceof NumberFormatException) {
//			System.out.println("asasssa");
//	        response.sendError((HttpServletResponse.SC_NOT_FOUND), authException.getMessage());
//	    }
//		
//		 

	}
}