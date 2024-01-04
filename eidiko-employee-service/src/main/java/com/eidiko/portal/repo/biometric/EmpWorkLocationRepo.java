package com.eidiko.portal.repo.biometric;

import com.eidiko.portal.entities.biometric.EmployeeWorkingLocationBiometric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpWorkLocationRepo extends JpaRepository<EmployeeWorkingLocationBiometric, Long>{

	List<EmployeeWorkingLocationBiometric> findAllByEmpId(long empId);

}
