package com.eidiko.portal.controller.employee;

import com.eidiko.portal.dto.employee.*;
import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import com.eidiko.portal.helper.employee.AuthAssignedConstants;
import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.service.employee.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.sasl.AuthenticationException;
import java.util.Map;
import java.util.regex.Matcher;

@RestController
@RequestMapping("/api/v1/access")
public class AcessLvlController {

	@Autowired
	private EmployeeService employeeService;

	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@PostMapping("/employee/add-shift-timing")
	public ResponseEntity<Map<String, Object>> addShiftTimingsAccLvl(
			@Valid @RequestBody ShiftTimingReqDto shiftTimingReqDto) throws AuthenticationException {

		if (shiftTimingReqDto.getEmpId() == 0) {
			throw new ResourceNotProcessedException(ConstantValues.EMPLOYEE_ID_MUST_NOT_BE_NULL_OR_BLANK);
		}
		if (shiftTimingReqDto.getWeekOff().size() > 2)
			throw new ResourceNotProcessedException(ConstantValues.WEEKOFF_CAN_ONLY_BE_USED_FOR_TWO_DAYS);
		return ResponseEntity.ok().body(this.employeeService.addShiftTiming(shiftTimingReqDto));

	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PostMapping("employee/add-reporting-manager")
	public ResponseEntity<Map<String, Object>> addReportingManager(@RequestBody ManagerRequestDto managerRequestDto)
			throws AuthenticationException {
		if (managerRequestDto.getEmpId() == 0) {
			throw new ResourceNotProcessedException(ConstantValues.EMPLOYEE_ID_MUST_NOT_BE_NULL_OR_BLANK);
		}
		if (managerRequestDto.getEmpId() == managerRequestDto.getManagerId())
			throw new ResourceNotProcessedException(ConstantValues.EMPLOYEE_AND_MANAGER_SHOULD_NOT_BE_THE_SAME);

		return ResponseEntity.ok().body(this.employeeService.addReportingManager(managerRequestDto));
	}

	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@PostMapping("employee/add-work-location")
	public ResponseEntity<Map<String, Object>> addWorkLocation(@RequestBody EmpWorkLocationReqDto empWorkLocationReqDto)
			throws NumberFormatException, AuthenticationException {
		if (empWorkLocationReqDto.getEmpId() == 0) {
			throw new ResourceNotProcessedException(ConstantValues.EMPLOYEE_ID_MUST_NOT_BE_NULL_OR_BLANK);
		}
		return ResponseEntity.ok().body(this.employeeService.addWorkLocation(empWorkLocationReqDto));
	}

	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@GetMapping("employee/get-shift-timings/{empId}")
	public ResponseEntity<Map<String, Object>> getEmployeeShiftTimingsRecord(@PathVariable long empId) {

		return ResponseEntity.ok(this.employeeService.getEmpShiftTimingRecord(empId));
	}

	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@GetMapping("employee/get-reporting-manager/{empId}")
	public ResponseEntity<Map<String, Object>> getEmployeeReportingManagerRecord(@PathVariable long empId) {

		return ResponseEntity.ok(this.employeeService.getEmpReportingManagerRecord(empId));
	}

	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@GetMapping("employee/get-work-location/{empId}")
	public ResponseEntity<Map<String, Object>> getEmployeeWorkingLocationRecord(@PathVariable long empId) {

		return ResponseEntity.ok(this.employeeService.getEmpWorkLocationRecord(empId));
	}

	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@GetMapping("employee/get-reported-employees/{empId}")
	public ResponseEntity<Map<String, Object>> getReportedEmployeeRecord(@PathVariable long empId) {

		return ResponseEntity.ok(this.employeeService.getEmpReportedEmployeeRecord(empId));
	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PutMapping("employee/update-employee")
	public ResponseEntity<Map<String, Object>> updateEmployee(@Valid @RequestBody EmployeeDto employeeDto)
			throws AuthenticationException {
		return ResponseEntity.ok().body(this.employeeService.updateEmployee(employeeDto));
	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@DeleteMapping("employee/delete-employee/{empId}")
	public ResponseEntity<Map<String, Object>> deleteEmployee(@PathVariable(ConstantValues.EMP_ID) long empId)
			throws AuthenticationException {
		return ResponseEntity.ok().body(this.employeeService.deleteEmployee(empId));
	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PostMapping("employee/update-contactno/{empId}")
	public ResponseEntity<Map<String, Object>> updateContactNo(@RequestParam String contactNo, @PathVariable long empId)
			throws NumberFormatException {
		java.util.regex.Pattern ptrn = java.util.regex.Pattern.compile("(0/91)?[5-9][0-9]{9}");
		Matcher match = ptrn.matcher(contactNo);
		if (match.find() && match.group().equals(contactNo)) {
			return ResponseEntity.ok(this.employeeService.updateEmployeeContactNo(contactNo, empId));
		} else
			throw new ResourceNotProcessedException(ConstantValues.PLEASE_ENTER_VALID_CONTACT_NUMBER);

	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PutMapping("employee/update-reporting-manager")
	public ResponseEntity<Map<String, Object>> updateReportingManager(@RequestBody ManagerRequestDto managerRequestDto)
			throws NumberFormatException {

		return ResponseEntity.ok().body(this.employeeService.updateReportingManager(managerRequestDto, false));
	}

	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@PutMapping("employee/update-working-location")
	public ResponseEntity<Map<String, Object>> updateWorkingLocation(
			@RequestBody EmpWorkLocationReqDto empWorkLocationReqDto) throws NumberFormatException {
		return ResponseEntity.ok().body(this.employeeService.updateWorkingLocation(empWorkLocationReqDto, false));
	}

	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@PutMapping("employee/update-shift-timing")
	public ResponseEntity<Map<String, Object>> updateShiftTiming(
			@Valid @RequestBody ShiftTimingReqDto shiftTimingReqDto) throws NumberFormatException {
		if (shiftTimingReqDto.getWeekOff().size() > 2)
			throw new ResourceNotProcessedException(ConstantValues.WEEKOFF_CAN_ONLY_BE_USED_FOR_TWO_DAYS);

		return ResponseEntity.ok().body(this.employeeService.updateShiftTimings(shiftTimingReqDto, false));
	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PostMapping("employee/add-from-xls")
	public ResponseEntity<Map<String, Object>> addEmployeeFromExcel(@RequestParam MultipartFile file)
			throws AuthenticationException {

		Map<String, Object> addEmployeeFromExcel = this.employeeService.addEmployeeFromExcel(file);
		return ResponseEntity.ok(addEmployeeFromExcel);
	}

	@PreAuthorize(AuthAssignedConstants.ADMIN_LEVEL_ACCESS)
	@PostMapping("employee/access-level")
	public ResponseEntity<Map<String, Object>> addAccessLevel(
			@RequestBody EmpAccessLevMappingRequestDto empAccessLevMappingRequestDto) {
		Map<String, Object> accessLevel = this.employeeService.addAccessLevel(empAccessLevMappingRequestDto.getEmpId(),
				empAccessLevMappingRequestDto.getAccessLvlId());
		return ResponseEntity.ok(accessLevel);
	}

	@PreAuthorize(AuthAssignedConstants.ADMIN_LEVEL_ACCESS)
	@DeleteMapping("employee/delete-access-level/{empId}/{accessLvlId}")
	public ResponseEntity<Map<String, Object>> deleteAccessLvl(@PathVariable("accessLvlId") long accessLvlId,
			@PathVariable long empId) {
		Map<String, Object> deleteEmpAccess = this.employeeService.deleteEmpAccess(accessLvlId, empId);
		return ResponseEntity.ok(deleteEmpAccess);
	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PostMapping("employee/update-profile-pic/{empId}")
	public ResponseEntity<Map<String, Object>> updateOrAddProfilePic(@RequestParam MultipartFile file,
			@PathVariable long empId) throws NumberFormatException, AuthenticationException {

		return ResponseEntity.ok(this.employeeService.updateProfilePic(file, empId));
	}

	@PreAuthorize(AuthAssignedConstants.ADMIN_LEVEL_ACCESS)
	@GetMapping("employee/byaccess/{accessLvlId}")
	public ResponseEntity<Map<String, Object>> getEmpByAccessLvl(@PathVariable("accessLvlId") long accessLvlId) {
		Map<String, Object> deleteEmpAccess = this.employeeService.getEmpByAccessLvl(accessLvlId);
		return ResponseEntity.ok(deleteEmpAccess);
	}
	
	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@GetMapping("get-employees-by-worklocation/{workingfrom}")
	public ResponseEntity<Map<String , Object>> getEmployeesByWorkLocation(@PathVariable String workingfrom )
	{
		Map<String,Object> empByWorkLocation = employeeService.getEmpByWorkLocation(workingfrom);
		return ResponseEntity.ok(empByWorkLocation);
	}

}
