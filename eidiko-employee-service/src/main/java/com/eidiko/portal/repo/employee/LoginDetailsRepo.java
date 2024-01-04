package com.eidiko.portal.repo.employee;

import com.eidiko.portal.entities.employee.LoginDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginDetailsRepo extends JpaRepository<LoginDetails, Long>{

	//public Employee findByEmployee(Employee employee);
	
	
}
