package com.eidiko.portal.repo.reportingmanager;

import com.eidiko.portal.entities.reportingmanager.ReportingManager_RM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportingManagerRepoRM extends JpaRepository<ReportingManager_RM, Long>{
	
	@Query(value = "SELECT rm.*\r\n"
			+ "FROM reporting_manager rm\r\n"
			+ "INNER JOIN (\r\n"
			+ "    SELECT emp_id\r\n"
			+ "    FROM reporting_manager\r\n"
			+ "    WHERE manager_id = ?1\r\n"
			+ "    GROUP BY emp_id\r\n"
			+ ") AS subquery\r\n"
			+ "ON rm.emp_id = subquery.emp_id;", nativeQuery = true)
	public List<ReportingManager_RM> getReportingManagerByGrouping(long managerId);
	
	public List<ReportingManager_RM> findAllByManagerId(long managerId);
	
	public List<ReportingManager_RM> findAllByEmpId(long empId);
	
	
	

}
