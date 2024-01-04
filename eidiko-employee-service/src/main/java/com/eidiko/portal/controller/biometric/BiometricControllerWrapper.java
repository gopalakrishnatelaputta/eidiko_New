package com.eidiko.portal.controller.biometric;

import com.eidiko.portal.config.employee.SecurityUtil;
import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import com.eidiko.portal.helper.employee.AuthAssignedConstants;
import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.helper.employee.Helper;
import com.eidiko.portal.service.biometric.BiometricReportService;
import com.eidiko.portal.service.biometric.BiometricService;
import com.eidiko.portal.service.biometric.BiometricServiceGetBioDates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.sasl.AuthenticationException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/biometric")
public class BiometricControllerWrapper {
	@Autowired
	BiometricService biometricService;
	@Autowired
	BiometricReportService biometricReportService;
	@Autowired
	BiometricServiceGetBioDates biometricServiceGetBioDates;

	@Autowired
	private Helper helper;

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PostMapping("/add-file")
	public Map<String, Object> addFile(@RequestParam MultipartFile file) {

		return this.biometricService.uploadFile(file);
	}

	private boolean validateUser(long empId) {
		try {
			if (SecurityUtil.getCurrentUserDetails().getEmpId() == empId || this.helper.validateUserOrAuth()) {
				return true;
			}
		} catch (AuthenticationException e) {
			throw new ResourceNotProcessedException("You don't have Authority");
		}
		return false;
	}

	@GetMapping("/get-biometric-report-empid-fromdate-todate/{empId}/{fromDate}/{toDate}")
	public Map<String, Object> getBiometricDataByEmpIdFromDateToDate(@PathVariable long empId,
			@PathVariable Date fromDate, @PathVariable Date toDate, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "1000") Integer pageSize,
			@RequestParam(defaultValue = ConstantValues.EMP_ID) String sortBy) {
		if (!validateUser(empId)) {
			throw new ResourceNotProcessedException("You don't have Authority");
		}
		Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by(sortBy));
		Timestamp startDate = Timestamp.valueOf(fromDate.toLocalDate().atStartOfDay());
		Timestamp endDate = Timestamp.valueOf(toDate.toLocalDate().atStartOfDay());
		return this.biometricReportService.getBioReportsFromDatetoTodateforEmp(empId,fromDate,toDate,pageable);
	}

	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@GetMapping("/get-biometric-report-bydate/{date}")
	public Map<String, Object> getAllBiometricReportsByDate(@PathVariable Date date,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "5") Integer pageSize,
			@RequestParam(defaultValue = ConstantValues.EMP_ID) String sortBy) {

		Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by(sortBy));
		return this.biometricReportService.getAllBiometricReportsByDate(date,pageable);
	}

	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@GetMapping("/get-biometric-report-bymonthandyearcount/{month}/{year}")
	public Map<String, Object> getBiometricReportsByMonthAndIsLateCount(@PathVariable String month,
			@PathVariable Integer year, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "5") Integer pageSize,
			@RequestParam(defaultValue = ConstantValues.EMP_ID) String sortBy) {
		Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by(sortBy));
		return this.biometricReportService.getCountofisLateBiometricReportByMonth(month,year,pageable);
	}

//not there
	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@GetMapping("/get-biometric-report-bymonthandyear/{month}/{year}")
	public Map<String, Object> getBiometricReportsByMonthAndIsLate(@PathVariable String month,
			@PathVariable Integer year, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "5") Integer pageSize,
			@RequestParam(defaultValue = ConstantValues.EMP_ID) String sortBy) {

		Pageable pageable = PageRequest.of(pageNo,pageSize,Sort.by(sortBy));
		return this.biometricReportService.getCountofisLateBiometricReportByMonth(month,year,pageable);
	}

	@GetMapping("/get-biometric-data-by-id-date/{empId}/{fromDate}/{toDate}")
	public Map<String, Object> getBiometricDataByEmpIds(
			@PathVariable("fromDate") @DateTimeFormat(iso = ISO.DATE_TIME) String fromDate,
			@PathVariable("toDate") @DateTimeFormat(iso = ISO.DATE_TIME) String toDate,
			@PathVariable("empId") long empId) {
		System.out.println("Inside getBiometricDataByEmpIds method");
		LocalDate todate = LocalDate.parse(toDate).plusDays(1);
		LocalDate fromDate1 = LocalDate.parse(fromDate);
		Timestamp timestamp = Timestamp.valueOf(todate.atStartOfDay());
		Timestamp timestamp1 = Timestamp.valueOf(fromDate1.atStartOfDay());
		//System.out.println(timestamp1 +"=======" + timestamp);
		return biometricServiceGetBioDates.getBiometricDataByEmpId(timestamp1,timestamp  , empId);
	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PutMapping("/update-islate-report/{month}/{year}")
	public ResponseEntity<Map<String, Object>> updateBiometricIsLateReport(@PathVariable int month,
			@PathVariable int year) throws SQLException {
		LocalDate fromDate;
		LocalDate toDate;
		if(month==12){
			YearMonth yearMonth = YearMonth.of(year, month);
			fromDate = yearMonth.atDay(1);
			YearMonth yearMonth1 = YearMonth.of(year+1, 1);
			toDate = yearMonth1.atDay(1);
		}
		else {
			YearMonth yearMonth = YearMonth.of(year, month);
			fromDate = yearMonth.atDay(1);
			YearMonth yearMonth1 = YearMonth.of(year, month + 1);
			toDate = yearMonth1.atDay(1);
		}
		Timestamp startDate = Timestamp.valueOf(fromDate.atStartOfDay());
		Timestamp endDate = Timestamp.valueOf(toDate.atStartOfDay());
		return ResponseEntity.ok(this.biometricReportService.updateBiometricIsLateReport(startDate,endDate));

	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@GetMapping("/get-all-biometric-report/calculated-report/{fromDate}/{toDate}")
	public Map<String, Object> getAllBiometricReportCalculated(@PathVariable String fromDate,
			@PathVariable String toDate, @RequestParam(defaultValue = "1000") Integer pageSize,
			@RequestParam(defaultValue = "0") Integer pageNo) {

	Pageable pageable = PageRequest.of(pageNo,pageSize);
		return this.biometricReportService.calculatedBiometricEmployeesReport(pageable,fromDate,toDate);

	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@GetMapping("/get-biometric-reports-view/{empId}/{year}")
	public Map<String, Object> getBiometricReportsView(@PathVariable Long empId, @PathVariable Integer year) {
		if (!validateUser(empId)) {
			throw new ResourceNotProcessedException("You don't have Authority");
		}

		return this.biometricReportService.biometricReportView(empId,year);
	}

	
	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PutMapping("/update-employee-missing-report/{modifiedBy}/{month}/{year}")
	public ResponseEntity<Map<String, Object>> updateMissingReport(@PathVariable int month,
			@PathVariable int year, @PathVariable long modifiedBy) throws SQLException {

		String startDate = "";
        String endDate = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (month == 0 && year == 2023) {
            startDate = "2023-01-01";
            endDate = "2023-01-25";
        } 
        else if (month == 0) {
        	String formattedMonth = String.format("%02d", month + 1); 
			startDate = (year - 1) + "-12-26";
			endDate = year + "-" + formattedMonth + "-25";
		}
		else if(month==10||month==11) {
			startDate = year + "-" + month + "-26";
			endDate = year + "-" + (month + 1) + "-25";
		}
		else if(month == 9) {
			String formattedMonth = String.format("%02d", month); 
			startDate = year + "-" + formattedMonth + "-26";
			endDate = year + "-" + (month + 1) + "-25";
		}
		else {
			String formattedMonth = String.format("%02d", month); 
			String formattedMonth1 = String.format("%02d", month + 1); 
			startDate = year + "-" + formattedMonth + "-26";
			endDate = year + "-" + formattedMonth1 + "-25";
		}

        LocalDate fromDate = LocalDate.parse(startDate, formatter);
        LocalDate toDate = LocalDate.parse(endDate, formatter);
		Map<String, Object> result = this.biometricReportService.updateEmployeesBiometricMissingReport(fromDate,toDate,modifiedBy);

		return ResponseEntity.ok().body(result);
	}


	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@GetMapping("/get-missing-report-employees-yearly/{year}")
	public ResponseEntity<Map<String, Object>> getMissingReportforAllEmployeesYearly(@PathVariable int year)
			throws SQLException {

		Map<String, Object> result = this.biometricReportService.getMissingReportforAllEmployeesYearly(year);
		return ResponseEntity.ok().body(result);
	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@GetMapping("/get-missing-report-employees-monthly/{year}/{month}")
	public ResponseEntity<Map<String, Object>> getMissingReportforAllEmployeesMonthly(@PathVariable int year,
			@PathVariable int month) throws SQLException {

		Map<String, Object> result = this.biometricReportService.getMissingReportforAllEmployeesMonthly(year,month);

		return ResponseEntity.ok().body(result);
	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@GetMapping("/get-missing-report-employee/{year}/{empId}")
	public ResponseEntity<Map<String, Object>> getMissingReportforEmployeeYearly(@PathVariable int year,
			@PathVariable long empId) throws SQLException {

		if (!validateUser(empId)) {
			throw new ResourceNotProcessedException("You don't have Authority");
		}


		Map<String, Object> result = this.biometricReportService.getMissingReportforEmployeeYearly(year,empId);

		return ResponseEntity.ok().body(result);
	}

}
