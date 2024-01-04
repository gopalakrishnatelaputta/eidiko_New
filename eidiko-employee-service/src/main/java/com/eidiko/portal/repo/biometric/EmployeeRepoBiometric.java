package com.eidiko.portal.repo.biometric;

import com.eidiko.portal.entities.biometric.EmployeeBiometric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepoBiometric extends JpaRepository<EmployeeBiometric, Long> {
	
	
	
	public List<EmployeeBiometric> findAllByIsDeleted(boolean isDeleted);

}
