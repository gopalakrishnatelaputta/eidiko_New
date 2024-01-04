package com.eidiko.portal.controller.leaves;

import com.eidiko.portal.config.employee.SecurityUtil;
import com.eidiko.portal.dto.leaves.EmployeeLeaveStatusDto;
import com.eidiko.portal.dto.leaves.EmployeeLeavesDto;
import com.eidiko.portal.entities.leaves.EmployeeLeaveStatusLeaves;
import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import com.eidiko.portal.helper.employee.AuthAssignedConstants;
import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.helper.employee.Helper;
import com.eidiko.portal.service.leaves.LeavesServiceWrapped;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.sasl.AuthenticationException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/leaves")
public class LeavesControllerWrapped {

	@Autowired
	private LeavesServiceWrapped leavesService;
	
	@Autowired
	private Helper helper;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	private boolean validateUser(long empId) {
		try {
			if(SecurityUtil.getCurrentUserDetails().getEmpId() == empId || this.helper.validateUserOrAuth() ) {
				return true;
			}
		} catch (AuthenticationException e) {
			throw new ResourceNotProcessedException("You don't have Authority");
		}
		return false;
	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PostMapping("/attendance/upload-xls")
	public Map<String, Object> uploadAttendanceExcelSheet(@RequestParam MultipartFile file, @RequestParam String month,
			@RequestParam String year) throws Exception {

		String username = SecurityUtil.getCurrentUserDetails().getUsername();
		long modifiedBy = Long.parseLong(username);
		return this.leavesService.uploadAttendanceExcelSheet(file, month, year, modifiedBy);

	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@GetMapping("/get-spent-leave-count/{empId}/{year}")
	public Map<String, Object> getSpentLeaveCountByEmployees(@PathVariable long empId, @PathVariable int year)
			throws Exception {
		
		if(!validateUser(empId)) {
			throw new ResourceNotProcessedException("You don't have Authority");
		}

		return this.leavesService.getSpentLeaveCountForEmployees(empId,year);
	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@GetMapping("/get-employee-leave-status-report/{year}")
	public Map<String, Object> getEmployeeLeaveStatusReport(@PathVariable int year) throws Exception {

		return this.leavesService.getEmployeeLeaveStatusReport(year);
	}
	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@GetMapping("/get-employee-leave-status-report-monthly/{year}/{month}")
	public Map<String, Object> getEmployeeLeaveStatusReportMonthly(@PathVariable int year,@PathVariable int month) throws Exception {
		
		return this.leavesService.getEmployeeLeaveStatusReportMonthly(year,month);
	}
	
	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PostMapping("/add-leaves-as-per-band")
	public Map<String, Object> addLeavesAsPerBand(@RequestParam MultipartFile file,
			@RequestParam String year) throws Exception {

		String username = SecurityUtil.getCurrentUserDetails().getUsername();
		long modifiedBy = Long.parseLong(username);
		int year1 = Integer.parseInt(year);
		return this.leavesService.addLeavesAsPerBand(year1, file,modifiedBy );

	}
	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PostMapping("/add-leave")
	public Map<String,Object> addLeave(@RequestBody EmployeeLeaveStatusDto employeeLeaveStatusDto)
	{
		System.out.println(employeeLeaveStatusDto);
		EmployeeLeaveStatusLeaves leaveStatusLeaves = this.modelMapper.map(employeeLeaveStatusDto, EmployeeLeaveStatusLeaves.class);
		System.out.println(leaveStatusLeaves);
		return this.leavesService.addLeaves(leaveStatusLeaves);
	}


	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PutMapping("/update-leave/{leavesStatusId}")
	public Map<String,Object> updateLeave(@PathVariable("leavesStatusId") long leavesStatusId, @RequestBody EmployeeLeaveStatusDto dto)
	{
		return leavesService.updateLeaveStatusFields(leavesStatusId,dto);
	}


	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@DeleteMapping("/delete-leave/{leavesStatusId}")
	public Map<String,Object> deleteLeaveStatusByEmpIdAndLeaveDate(@PathVariable("leavesStatusId") long leaveStatusId) {
		return leavesService.deleteByEmpIdAndLeaveDate(leaveStatusId);
	}

}
