package com.eidiko.portal.controller.reportingmanager;

import com.eidiko.portal.helper.employee.AuthAssignedConstants;
import com.eidiko.portal.service.reportingmanager.ReportingManagerServiceWrapped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/reporting-manager")
public class ReportingManagerControllerWrapped {
	
	
	@Autowired
	private ReportingManagerServiceWrapped managerService;

	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@GetMapping("/get-reporting-employees/{managerId}/{fromDate}/{toDate}")
	public ResponseEntity<Map<String, Object>> getReportingEmployees(@PathVariable long managerId,
			@PathVariable String fromDate, @PathVariable String toDate) throws Exception {

		Map<String, Object> reportingEmployees = this.managerService.getReportingEmployees(managerId, fromDate, toDate);

		return ResponseEntity.ok().body(reportingEmployees);
	}
	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@GetMapping("/get-reporting-employee/detailed-information/{empId}/{managerId}/{year}")
	public ResponseEntity<Map<String, Object>> getReportingEmployeeDetailedInformation(@PathVariable long empId,
			@PathVariable long managerId, @PathVariable int year,@RequestHeader("Authorization") String bearerToken) throws Exception {

		Map<String, Object> reportingEmployeesDetailedInformation = this.managerService
				.getReportingEmployeesDetailedInformation(empId, managerId, year,bearerToken);

		return ResponseEntity.ok().body(reportingEmployeesDetailedInformation);
	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@GetMapping("/get-employees-without-manager")
	public ResponseEntity<Map<String, Object>> getAllEmployeesWithoutManager()  {

		Map<String, Object> reportingEmployeesDetailedInformation = this.managerService
				.getAllEmployeesWithoutManager();

		return ResponseEntity.ok().body(reportingEmployeesDetailedInformation);
	}

}
