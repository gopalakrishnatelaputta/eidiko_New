package com.eidiko.portal.config.employee;


import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.serviceimpl.employeeimpl.EmployeeDetailsSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	
		@Autowired
		private EmployeeDetailsSecurityService customUserDetailsService;
	
	   	@Autowired
	    private  JwtAuthenticationEntryPoint authenticationEntryPoint;

	    @Autowired
	    private JwtAuthenticationFilter authenticationFilter;
	
	    
	    
	    @Bean
	     AuthenticationProvider authenticationProvider() {
	         DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
	         authenticationProvider.setUserDetailsService(customUserDetailsService);
	        authenticationProvider.setPasswordEncoder(passwordEncoder());
	        return authenticationProvider;
	     }

	     @Bean
	      AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
	         return config.getAuthenticationManager();
	     }


	     @Bean
	      PasswordEncoder passwordEncoder() {
	         return new BCryptPasswordEncoder();
	     }


	private static final String[] AUTH_WHITELIST = { "/authenticate", "/swagger-resources/**", "/swagger-ui/**",
			"/v3/api-docs", "/webjars/**", "/api/v1/auth/**", "/api/v1/employee/search-employee/**",
			"/api/v1/employee/load-profile-pic/**","/api/v1/drop-down-list/**","/api/V1/**"

	};

	     @Bean
	      SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
	         httpSecurity.csrf().disable()
	                 .authorizeHttpRequests()
	                 .requestMatchers(AUTH_WHITELIST).permitAll()
	                 .anyRequest()
	                 .authenticated()
	                 .and()
	                 .exceptionHandling()
	                 .authenticationEntryPoint(this.authenticationEntryPoint)
	                 .and()
	                 .sessionManagement()
	                 .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

	         return httpSecurity.addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class)
	                 .build();

	     }

	     @Bean
	      FilterRegistrationBean<CorsFilter> coresFilter() {

	         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	         CorsConfiguration corsConfiguration = new CorsConfiguration();
	         corsConfiguration.setAllowCredentials(true);
	         corsConfiguration.addAllowedOriginPattern(ConstantValues.STAR);
	         corsConfiguration.addAllowedHeader(ConstantValues.AUTHORIZATION);
	         corsConfiguration.addAllowedHeader(ConstantValues.CONTENT_TYPE);
	         corsConfiguration.addAllowedHeader(ConstantValues.ACCEPT);
	         corsConfiguration.addAllowedMethod(ConstantValues.GET);
	         corsConfiguration.addAllowedMethod(ConstantValues.POST);
	         corsConfiguration.addAllowedMethod(ConstantValues.DELETE);
	         corsConfiguration.addAllowedMethod(ConstantValues.PUT);
	         corsConfiguration.addAllowedMethod(ConstantValues.OPTIONS);
	         corsConfiguration.setMaxAge(3600L);
	         source.registerCorsConfiguration(ConstantValues.PATTERN, corsConfiguration);

	         FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
	         bean.setOrder(-110);
	         return bean;

	     }


	    
	    
}
