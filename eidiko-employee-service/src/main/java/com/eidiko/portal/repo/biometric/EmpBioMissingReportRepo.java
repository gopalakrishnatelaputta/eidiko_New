package com.eidiko.portal.repo.biometric;


import com.eidiko.portal.entities.biometric.EmpBiometricMissingReport;
import com.eidiko.portal.helper.biometric.MissingReportProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EmpBioMissingReportRepo extends JpaRepository<EmpBiometricMissingReport, Long> {

	public static final String FIND_DDUPLICATES_IN_MISSING_REPORT_TABLE = "SELECT *\r\n"
			+ "    FROM `emp_biometric_missing_report`\r\n" + "    WHERE emp_bio_missing_id NOT IN\r\n" + "    (\r\n"
			+ "SELECT MAX(`emp_bio_missing_id`)\r\n" + "        FROM emp_biometric_missing_report\r\n"
			+ "        GROUP BY emp_id, `missing_date`\r\n" + "        )";

	public static final String GET_MISSING_REPORT_YEARLY = "SELECT e.EMP_NAME, m.emp_id, COUNT(*) AS total_missing_reports\r\n"
			+ "FROM emp_biometric_missing_report m\r\n" + "JOIN employee e ON e.EMP_ID = m.emp_id\r\n"
			+ "WHERE m.missing_year = ?1\r\n" + "GROUP BY m.emp_id ORDER BY total_missing_reports DESC";

	public static final String GET_MISSING_REPORT_MONTHLY = "SELECT e.EMP_NAME, m.emp_id, COUNT(*) AS total_missing_reports\r\n"
			+ "FROM emp_biometric_missing_report m\r\n" + "JOIN employee e ON e.EMP_ID = m.emp_id\r\n"
			+ "WHERE m.missing_year = ?1 and m.missing_month=?2 \r\n"
			+ "GROUP BY m.emp_id ORDER BY total_missing_reports DESC";

	@Query(value = FIND_DDUPLICATES_IN_MISSING_REPORT_TABLE, nativeQuery = true)
	public List<EmpBiometricMissingReport> getDuplicatesDataFromMissingReport();

	@Query(value = GET_MISSING_REPORT_YEARLY, nativeQuery = true)
	public List<MissingReportProjection> getMissingReportYearly(int year);

	@Query(value = GET_MISSING_REPORT_MONTHLY, nativeQuery = true)
	public List<MissingReportProjection> getMissingReportmonthly(int year, int month);
	
	
	public List<EmpBiometricMissingReport> findAllByEmpIdAndMissingYear(long empId,int year);

	public List<EmpBiometricMissingReport> findAllByMissingDateBetween(LocalDate fromDate, LocalDate toDate);
	
	

}
