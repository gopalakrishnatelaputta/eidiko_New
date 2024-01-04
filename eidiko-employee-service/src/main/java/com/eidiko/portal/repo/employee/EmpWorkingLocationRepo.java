package com.eidiko.portal.repo.employee;

import com.eidiko.portal.entities.employee.EmployeeWorkingLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmpWorkingLocationRepo extends JpaRepository<EmployeeWorkingLocation, Long> {
	
	List<EmployeeWorkingLocation> findAllByWorkingFrom(String workingfrom);

	@Query(value = "select * from emp_working_location where working_from = ?1 or working_from= ?2",nativeQuery = true)
	List<EmployeeWorkingLocation> findAllByWorkingFromClientLocation(String workingfrom, String worklocation);
}
