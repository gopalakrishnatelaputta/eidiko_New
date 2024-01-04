package com.eidiko.portal.repo.reportingmanager;

import com.eidiko.portal.entities.reportingmanager.DailyStatusReportReportingManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DailyStatusReportRepoRM extends JpaRepository<DailyStatusReportReportingManager, Long>{

	
	@Query(value = "SELECT * FROM `daily_status_report` WHERE emp_id=?1 AND `STATUS_REPORT_DATE` BETWEEN ?2 AND ?3", nativeQuery = true)
	List<DailyStatusReportReportingManager> findTaskStatusReportByEmpIdBetweenDates(long empId, String fromDate, String toDate);
	
	
}
