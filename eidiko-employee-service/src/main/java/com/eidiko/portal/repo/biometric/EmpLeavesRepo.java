package com.eidiko.portal.repo.biometric;

import com.eidiko.portal.entities.biometric.EmployeeLeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
@Repository
public interface EmpLeavesRepo extends JpaRepository<EmployeeLeaveStatus, Long>{

	//public boolean existsByEmpIdAndleaveDate(long empId, LocalDate date);
	
	
	boolean existsByEmpIdAndLeaveDate(long empId, LocalDate leaveDate);

}
