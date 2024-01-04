package com.eidiko.portal.controller.taskstatus;

import com.eidiko.portal.config.employee.SecurityUtil;
import com.eidiko.portal.dto.taskstatus.DailyStatusReportDto;
import com.eidiko.portal.dto.taskstatus.VerificationReqDto;
import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import com.eidiko.portal.exception.taskstatus.NotNullException;
import com.eidiko.portal.helper.employee.AuthAssignedConstants;
import com.eidiko.portal.helper.employee.Helper;
import com.eidiko.portal.helper.taskstatus.ConstantValues;
import com.eidiko.portal.service.taskstatus.TaskStatusServiceWrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/daily-status-report")
@Slf4j
public class TaskStatusReportControllerWrapped {

	@Autowired
	TaskStatusServiceWrapped taskStatusService;

	@Autowired
	private Helper helper;

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

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@GetMapping("/get-report/{empId}/{fromDate}/{toDate}")
	public Map<String, Object> getPosts(@PathVariable long empId, @PathVariable Date fromDate,
			@PathVariable Date toDate, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "5") Integer pageSize,
			@RequestParam(defaultValue = "taskDetailsId") String sortBy) throws JsonProcessingException {

//		String path = ConstantValues.TASK_STATUS_SERVICE_BASE_URL
//				+ ConstantValues.TASK_STATUS_SERVICE_GET_REPORT_FROMDATE_TODATE_RESOURCE + ConstantValues.URL_SEPARATOR
//				+ empId + ConstantValues.URL_SEPARATOR + fromDate + ConstantValues.URL_SEPARATOR + toDate + ConstantValues.PAGE_NO
//				+ pageNo + ConstantValues.PAGE_SIZE + pageSize + ConstantValues.SORT_BY + sortBy;
		// String
		// path="http://192.168.1.207:9292/api/V1/dailyStatusReport/get/1000/2023-02-10/2023-05-21?pageNo=0&pageSize=3";
		if (!validateUser(empId)) {
			throw new ResourceNotProcessedException("You don't have Authority");
		}
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
		Timestamp startDate = Timestamp.valueOf(fromDate.toLocalDate().atStartOfDay());
		Timestamp endDate = Timestamp.valueOf(toDate.toLocalDate().atStartOfDay());

		return this.taskStatusService.getDailyStatusReportFromDateToDate(empId,startDate, endDate, pageable);
	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@PostMapping("/create-task")
	public Map<String, Object> addTask(@RequestBody DailyStatusReportDto dailyStatusReportDto)
			throws URISyntaxException, JsonMappingException, JsonProcessingException, NotNullException {
		Long empId = null;
		try {
			empId = SecurityUtil.getCurrentUserDetails().getEmpId();
		} catch (AuthenticationException e) {
			throw new ResourceNotProcessedException(e.getMessage());
		}
		Map<String, Object> map = new HashMap<>();
	    this.taskStatusService.addTask(dailyStatusReportDto, empId);
		map.put(ConstantValues.MESSAGE , ConstantValues.CREATED);
		map.put(ConstantValues.STATUS_TEXT , HttpStatus.CREATED.value());
		map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
		log.info(ConstantValues.RESULT + map );
		return map;
	}

	@PreAuthorize(AuthAssignedConstants.ADMIN_LEVEL_ACCESS)
	@PutMapping("/update-verified-by")
	public Map<String, Object> updateVerifiedBy(@RequestBody VerificationReqDto verificationReqDto) {

		// http://192.168.1.207:9292/api/V1/dailyStatusReport/updateAll

		try {
			System.out.println(SecurityUtil.getCurrentUserDetails().getEmpId());
			verificationReqDto.setVerifiedById(SecurityUtil.getCurrentUserDetails().getEmpId());
		} catch (AuthenticationException e) {
			throw new ResourceNotProcessedException(e.getMessage());
		}
		Map<String, Object> map = new HashMap<>();
		this.taskStatusService.updateVerifiedBy(verificationReqDto.getTaskDetailsId(), verificationReqDto.getVerifiedById());
		map.put(ConstantValues.MESSAGE, ConstantValues.UPDATED);
		map.put(ConstantValues.STATUS_TEXT , HttpStatus.OK.value());
		map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
		return map;

	}

	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@GetMapping("/get-all-reports")
	public Map<String, Object> getAllReports(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "5") Integer pageSize,
			@RequestParam(defaultValue = "taskDetailsId") String sortBy) {
		// http://192.168.1.207:9292/api/V1/dailyStatusReport/get?pageNo=0&pageSize=10
		Pageable pageable = PageRequest.of(pageNo,pageSize,Sort.by(sortBy));
		return this.taskStatusService.getAllDailyStatusReports(pageable);
	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@GetMapping("/get-all-reports-empId/{empId}")
	public Map<String, Object> getAllReportsByEmpId(@PathVariable long empId,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "5") Integer pageSize,
			@RequestParam(defaultValue = "taskDetailsId") String sortBy) {
		// http://192.168.1.207:9292/api/V1/dailyStatusReport/get/1000?pageNo=0&pageSize=3
		if (!validateUser(empId)) {
			throw new ResourceNotProcessedException("You don't have Authority");
		}
		Pageable pageable = PageRequest.of(pageNo,pageSize,Sort.by(sortBy));
		return this.taskStatusService.getAllReportsByEmpId( empId,  pageable);
	}

	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@GetMapping("/get-all-reports-between-given-dates/{fromDate}/{toDate}")
	public Map<String, Object> getAllReportsGivenDates(@PathVariable Date fromDate, @PathVariable Date toDate,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "5") Integer pageSize,
			@RequestParam(defaultValue = "taskDetailsId") String sortBy) {
		// http://192.168.1.207:9292/api/V1/dailyStatusReport/get/2023-03-14/2023-05-20?pageNo=0&pageSize=3
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
		Timestamp startDate = Timestamp.valueOf(fromDate.toLocalDate().atStartOfDay());
		Timestamp endDate = Timestamp.valueOf(toDate.toLocalDate().atStartOfDay());
		return this.taskStatusService.getAllReportsGivenDates(startDate,endDate,pageable);
	}

	@PreAuthorize(AuthAssignedConstants.ADMIN_LEVEL_ACCESS)
	@GetMapping("/get-pending-reports-by-date/{fromDate}/{toDate}")
	public Map<String, Object> getPendingReportsByDate(@PathVariable Date fromDate, @PathVariable Date toDate,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "5") Integer pageSize,
			@RequestParam(defaultValue = "taskDetailsId") String sortBy) {
		// http://192.168.1.207:9292/api/V1/dailyStatusReport/getPending/2023-03-14/2023-05-03
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
		Timestamp startDate = Timestamp.valueOf(fromDate.toLocalDate().atStartOfDay());
		Timestamp endDate = Timestamp.valueOf(toDate.toLocalDate().atStartOfDay());
		return this.taskStatusService.getPendingReports(startDate,endDate);
	}

	@PreAuthorize(AuthAssignedConstants.ADMIN_LEVEL_ACCESS)
	@GetMapping("/get-pending-status/{fromDate}/{toDate}")
	public Map<String, Object> getStatus(@PathVariable Date fromDate, @PathVariable Date toDate,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "5") Integer pageSize,
			@RequestParam(defaultValue = "No") String status,
			@RequestParam(defaultValue = "taskDetailsId") String sortBy) {

		// http://192.168.1.207:9292/api/V1/dailyStatusReport/getStatus/2023-03-14/2023-05-02?status=No&pageNo=0&pageSize=10
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
		Timestamp startDate = Timestamp.valueOf(fromDate.toLocalDate().atStartOfDay());
		Timestamp endDate = Timestamp.valueOf(toDate.toLocalDate().atStartOfDay());
		return this.taskStatusService.getStatusReport(startDate,endDate,status,pageable);

	}

	@PreAuthorize(AuthAssignedConstants.ADMIN_LEVEL_ACCESS)
	@GetMapping("/get-all-pending-status")
	public Map<String, Object> getAllPendingStatus(@RequestParam(defaultValue = "taskDetailsId") String sortBy,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "5") Integer pageSize,  @RequestParam("status") String status){
		// String
		// path="http://192.168.1.207:9292/api/V1/dailyStatusReport/getAllPendingStatus?status=no";
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
		return this.taskStatusService.getAllPendingStatus(status,pageable);
	}

	@PreAuthorize(AuthAssignedConstants.ADMIN_LEVEL_ACCESS)
	@GetMapping("/get-all-pending-reports")
	public Map<String, Object> getAllPendingReports(@RequestParam(defaultValue = "taskDetailsId") String sortBy,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "5") Integer pageSize) {
		// String
		// path="http://192.168.1.207:9292/api/V1/dailyStatusReport/getAllPendingReports";

		
		return this.taskStatusService.getAllPendingReports();
	}

}
