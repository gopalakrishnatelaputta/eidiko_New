package com.eidiko.portal.repo.reportingmanager;

import com.eidiko.portal.entities.reportingmanager.EmployeeLeaveStatusReportingManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeLeaveStatusRepoRM extends JpaRepository<EmployeeLeaveStatusReportingManager, Long>{

	
	@Query(value = "select * from employee_leave_status where emp_id=?3 and leave_date between ?1 and ?2", nativeQuery = true)
	public List<EmployeeLeaveStatusReportingManager> findAllByEmpIdInThisYear(String fromDate,String toDate,long empId); 
	
}
