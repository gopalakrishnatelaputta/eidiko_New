package com.eidiko.portal.serviceimpl.employeeimpl;


import com.eidiko.portal.exception.employee.ResourceNotFoundException;
import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.repo.employee.EmployeeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class EmployeeDetailsSecurityService implements UserDetailsService {

	@Autowired
	private EmployeeRepo employeeRepo;
	
	
	private Logger logger =LoggerFactory.getLogger(getClass());

	@Override
	public UserDetails loadUserByUsername(String empId) throws UsernameNotFoundException {
		try {
			logger.info(ConstantValues.CUSTOM_USER_DETAILS);
			return this.employeeRepo.findById(Long.parseLong(empId)).orElseThrow(()-> new ResourceNotFoundException(ConstantValues.EMPLOYEE_NOT_FOUND_WITH+empId)) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; 
		
	}
	
	
	
	
	
	
}
