package com.eidiko.portal.config.employee;

import com.eidiko.portal.helper.employee.ConstantValues;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenHelper jwtTokenHelper;

	private final Logger custLogger = LoggerFactory.getLogger(getClass());

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		String requestToken = request.getHeader(ConstantValues.AUTHORIZATION);
		String userName = null;
		String token = null;

		if (requestToken != null && requestToken.startsWith(ConstantValues.BEARER)) {

			token = requestToken.substring(7);
			try {
				userName = this.jwtTokenHelper.getUserNameFromToken(token);

			} catch (IllegalArgumentException | ExpiredJwtException | MalformedJwtException | SignatureException e) {
				//e.printStackTrace();
				//throw new TokenNotValidException("jwt token not valid");
			}

		} else {
			custLogger.warn(ConstantValues.JWT_TOKEN_DOES_NOT_BEGIN_WITH_BEARER);
		}

		// Validate Token
		if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
			if (this.jwtTokenHelper.validateToken(token, userDetails)) {
				// working ! do authentication
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);

			} else {
				custLogger.error(ConstantValues.INVALID_TOKEN_JWT);
			}
		} else {
			custLogger.error(ConstantValues.JWT_TOKEN_NOT_VALID);
//			throw new JwtA("Token not valid");
		}

	
			filterChain.doFilter(request, response);
		

	}
}
