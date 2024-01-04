package com.eidiko.portal.repo.reportingmanager;


import com.eidiko.portal.entities.reportingmanager.EmployeeReportingManager;
import com.eidiko.portal.helper.biometric.BiometricReportProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepoReportingManager extends JpaRepository<EmployeeReportingManager, Long>{

	
	
	@Query(value = "SELECT COUNT(*) AS COUNT, r.emp_id, e.emp_name\r\n"
			+ "FROM emp_biometric_report r, employee e\r\n"
			+ "WHERE total_working_time > 540\r\n"
			+ "  AND r.biometric_date BETWEEN ?1 AND ?2 \r\n"
			+ "  AND r.emp_id = e.emp_id\r\n"
			+ "  AND r.emp_id = ?3 \r\n"
			+ "GROUP BY r.emp_id\r\n"
			+ "ORDER BY COUNT(*) DESC", nativeQuery = true)
	BiometricReportProjection findBytotalworkingtime(String fromDate, String toDate, long empId);

	@Query(value = "SELECT COUNT(*) AS COUNT, r.emp_id, e.emp_name \r\n"
			+ "FROM emp_biometric_report r, employee e \r\n"
			+ "WHERE is_Late = 0 AND r.biometric_date BETWEEN ?1 AND ?2\r\n"
			+ " AND r.emp_id = e.emp_id \r\n"
			+ " AND r.emp_id = ?3 \r\n"
			+ " GROUP BY r.emp_id \r\n"
			+ " ORDER BY COUNT(*) DESC", nativeQuery = true)
	BiometricReportProjection findByisLatefalse(String fromDate, String toDate, long empId);

	@Query(value = "SELECT COUNT(*) AS COUNT, r.emp_id, e.emp_name \r\n"
			+ "FROM emp_biometric_report r, employee e \r\n"
			+ "WHERE is_Late = 1 AND r.biometric_date BETWEEN ?1 AND ?2\r\n"
			+ " AND r.emp_id = e.emp_id \r\n"
			+ " AND r.emp_id = ?3 \r\n"
			+ " GROUP BY r.emp_id \r\n"
			+ " ORDER BY COUNT(*) DESC", nativeQuery = true)
	BiometricReportProjection findByisLatetrue(String fromDate, String toDate, long empId);
	
	

	@Query(value = "SELECT COUNT(*) AS COUNT, r.emp_id, e.emp_name\r\n"
			+ "FROM emp_biometric_report r, employee e\r\n"
			+ "WHERE total_working_time < 540\r\n"
			+ "  AND r.biometric_date BETWEEN ?1 AND ?2 \r\n"
			+ "  AND r.emp_id = e.emp_id\r\n"
			+ "  AND r.emp_id = ?3 \r\n"
			+ "GROUP BY r.emp_id\r\n"
			+ "ORDER BY COUNT(*) DESC", nativeQuery = true)
	BiometricReportProjection findBytotalworkingtimeLess9hrs(String fromDate, String toDate,
			long empId);

	@Query(value = "SELECT COUNT(*) AS COUNT, r.emp_id, e.emp_name \r\n"
			+ "FROM emp_biometric_report r, employee e \r\n"
			+ "WHERE is_very_Late = 1 AND r.biometric_date BETWEEN ?1 AND ?2\r\n"
			+ " AND r.emp_id = e.emp_id \r\n"
			+ " AND r.emp_id = ?3 \r\n"
			+ " GROUP BY r.emp_id \r\n"
			+ " ORDER BY COUNT(*) DESC", nativeQuery = true)
	BiometricReportProjection findByisVeryLatetrue(String fromDate, String toDate, long empId);
	
	@Query(value = "select emp_name from employee where emp_id=?1", nativeQuery = true)
	String getNameFromEmployee(long empId);
	
	
	@Query(value = "select emp_id from employee where is_deleted = true", nativeQuery = true)
	public List<Long> getAllNonDeletedEmployees();
	
}
