package com.eidiko.portal.service.biometric;

import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Map;

public interface BiometricReportService {

	public Map<String, Object> getAllBiometricReportsByDate(Date date, Pageable pageable);

	public Map<String, Object> getBioReportsFromDatetoTodateforEmp(long empId, Date fromDate, Date toDate,
			Pageable pageable);

	public Map<String, Object> getCountofisLateBiometricReportByMonth(String month, Integer year, Pageable pageable);

	public Map<String, Object> updateBiometricIsLateReport(Timestamp fromDate, Timestamp toDate) throws SQLException;

	public Map<String, Object> calculatedBiometricEmployeesReport(Pageable pageable, String fromDate, String toDate);

	public Map<String, Object> biometricReportView(long empId, Integer year);

	public Map<String, Object> updateEmployeesBiometricMissingReport(LocalDate fromDate,LocalDate toDate,
			long modifiedBy) throws SQLException;

	public Map<String, Object> getMissingReportforAllEmployeesYearly(int year) throws SQLException;

	public Map<String, Object> getMissingReportforAllEmployeesMonthly(int year, int month) throws SQLException;

	public Map<String, Object> getMissingReportforEmployeeYearly(int year, long empId) throws SQLException;

}
