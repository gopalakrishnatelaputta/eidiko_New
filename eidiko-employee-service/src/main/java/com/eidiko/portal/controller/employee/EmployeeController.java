package com.eidiko.portal.controller.employee;


import com.eidiko.portal.config.employee.SecurityUtil;
import com.eidiko.portal.dto.employee.*;
import com.eidiko.portal.entities.employee.Employee;
import com.eidiko.portal.entities.employee.ResignedEmployee;
import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import com.eidiko.portal.exception.employee.TokenNotValidException;
import com.eidiko.portal.helper.employee.AuthAssignedConstants;
import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.helper.employee.FilterResignedEmp;
import com.eidiko.portal.service.employee.EmployeeService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.sasl.AuthenticationException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private FilterResignedEmp filterResignedEmp;

	@Value("${files.storage}")
	public String fileStorage;

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PostMapping("/create")
	public ResponseEntity<Map<String, Object>> createEmployee(@Valid @RequestBody EmployeeDto employeeDto)
			throws AuthenticationException {
		try {
			SecurityUtil.isAuthenticatedUser();
		} catch (AuthenticationException e) {
			throw new TokenNotValidException(ConstantValues.SESSION_HAS_BEEN_EXPIRED);
		}
		Map<String, Object> map = this.employeeService.createEmployee(this.mapper.map(employeeDto, Employee.class));
		return ResponseEntity.ok().body(map);
	}

	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@GetMapping("/get-all-employee")
	public ResponseEntity<Map<String, Object>> getAllEmployee(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "5") Integer pageSize,
			@RequestParam(defaultValue = ConstantValues.EMP_ID) String sortBy) throws AuthenticationException {
		SecurityUtil.isAuthenticatedUser();
		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
		List<Employee> allEmployee = this.employeeService.getAllEmployee(paging);
		Map<String, Object> map = new HashMap<>();
		if (!allEmployee.isEmpty()) {
			map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.RESULT, allEmployee);
		} else {
			map.put(ConstantValues.MESSAGE, ConstantValues.NO_DATA_FETCHED_SUCCESS_TEXT);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.RESULT, allEmployee);
		}
		return ResponseEntity.ok(map);
	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@PostMapping("/change-password")
	public ResponseEntity<Map<String, Object>> changePassword(@Valid @RequestBody ChangePasswordDto passwordDto)
			throws AuthenticationException {

		return ResponseEntity.ok(this.employeeService.changePassword(passwordDto));

	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@GetMapping("/get-employee/{empId}")
	public ResponseEntity<Map<String, Object>> getEmployeeById(@PathVariable long empId) throws AuthenticationException {
		return ResponseEntity.ok().body(this.employeeService.getEmployeeById(empId));
	}

	@GetMapping("/search-employee/{empId}")
	public ResponseEntity<Map<String, Object>> searchEmployeeById(@PathVariable String empId) {

		return ResponseEntity.ok().body(employeeService.searchEmployeeById(empId));
	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@PostMapping("/add-shift-timing")
	public ResponseEntity<Map<String, Object>> addShiftTimings(@Valid @RequestBody ShiftTimingReqDto shiftTimingReqDto)
			throws AuthenticationException {
		if (shiftTimingReqDto.getWeekOff().size() > 2)
			throw new ResourceNotProcessedException(ConstantValues.WEEKOFF_CAN_ONLY_BE_USED_FOR_TWO_DAYS);

		shiftTimingReqDto.setEmpId(Long.parseLong(SecurityUtil.getCurrentUserDetails().getUsername()));
		return ResponseEntity.ok().body(this.employeeService.addShiftTiming(shiftTimingReqDto));
	}
	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@PostMapping("/add-work-location")
	public ResponseEntity<Map<String, Object>> addWorkLocation(@RequestBody EmpWorkLocationReqDto empWorkLocationReqDto)
			throws NumberFormatException, AuthenticationException {

		empWorkLocationReqDto.setEmpId(Long.parseLong(SecurityUtil.getCurrentUserDetails().getUsername()));
		return ResponseEntity.ok().body(this.employeeService.addWorkLocation(empWorkLocationReqDto));
	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@PostMapping("/add-reporting-manager")
	public ResponseEntity<Map<String, Object>> addReportingManager(@RequestBody ManagerRequestDto managerRequestDto)
			throws AuthenticationException {

		managerRequestDto.setEmpId(Long.parseLong(SecurityUtil.getCurrentUserDetails().getUsername()));

		if (managerRequestDto.getEmpId() == managerRequestDto.getManagerId()) {
			throw new ResourceNotProcessedException(ConstantValues.EMPLOYEE_AND_MANAGER_SHOULD_NOT_BE_THE_SAME);
		}

		return ResponseEntity.ok().body(this.employeeService.addReportingManager(managerRequestDto));
	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@PostMapping("/add-about")
	public ResponseEntity<Map<String, Object>> addAbout(@RequestParam String about) throws AuthenticationException {

		return ResponseEntity.ok().body(this.employeeService.addAbout(about));
	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@GetMapping("get-shift-timings")
	public ResponseEntity<Map<String, Object>> getEmployeeShiftTimingsRecord()
			throws NumberFormatException, AuthenticationException {
		long empId = Long.parseLong(SecurityUtil.getCurrentUserDetails().getUsername());
		return ResponseEntity.ok(this.employeeService.getEmpShiftTimingRecord(empId));
	}
	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@GetMapping("get-reporting-manager")
	public ResponseEntity<Map<String, Object>> getEmployeeReportingManagerRecord()
			throws NumberFormatException, AuthenticationException {
		long empId = Long.parseLong(SecurityUtil.getCurrentUserDetails().getUsername());
		return ResponseEntity.ok(this.employeeService.getEmpReportingManagerRecord(empId));
	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@GetMapping("get-work-location")
	public ResponseEntity<Map<String, Object>> getEmployeeWorkingLocationRecord()
			throws NumberFormatException, AuthenticationException {
		long empId = Long.parseLong(SecurityUtil.getCurrentUserDetails().getUsername());
		return ResponseEntity.ok(this.employeeService.getEmpWorkLocationRecord(empId));
	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@GetMapping("get-reported-employees")
	public ResponseEntity<Map<String, Object>> getReportedEmployeeRecord()
			throws NumberFormatException, AuthenticationException {
		long empId = Long.parseLong(SecurityUtil.getCurrentUserDetails().getUsername());
		return ResponseEntity.ok(this.employeeService.getEmpReportedEmployeeRecord(empId));
	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@PostMapping("update-contactno")
	public ResponseEntity<Map<String, Object>> updateContactNo(@RequestParam String contactNo)
			throws NumberFormatException, AuthenticationException {
		java.util.regex.Pattern ptrn = java.util.regex.Pattern.compile("(0/91)?[5-9][0-9]{9}");
		Matcher match = ptrn.matcher(contactNo);
		if (match.find() && match.group().equals(contactNo)) {
			long empId = Long.parseLong(SecurityUtil.getCurrentUserDetails().getUsername());
			return ResponseEntity.ok(this.employeeService.updateEmployeeContactNo(contactNo, empId));
		} else
			throw new ResourceNotProcessedException(ConstantValues.PLEASE_ENTER_VALID_CONTACT_NUMBER);

	}
	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@PostMapping("update-profile-pic")
	public ResponseEntity<Map<String, Object>> updateOrAddProfilePic(@RequestParam MultipartFile file)
			throws NumberFormatException, AuthenticationException {

		long empId = Long.parseLong(SecurityUtil.getCurrentUserDetails().getUsername());
		return ResponseEntity.ok(this.employeeService.updateProfilePic(file, empId));
	}

//	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@GetMapping("load-profile-pic/{empId}")
	public ResponseEntity<Resource> loadProfilePic(@PathVariable long empId)
			throws NumberFormatException {

		Resource file = this.employeeService.loadProfile(empId);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(MediaType.IMAGE_JPEG_VALUE))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@PutMapping("/update-reporting-manager")
	public ResponseEntity<Map<String, Object>> updateReportingManager(@RequestBody ManagerRequestDto managerRequestDto)
			throws NumberFormatException {
		return ResponseEntity.ok().body(this.employeeService.updateReportingManager(managerRequestDto, true));
	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@PutMapping("/update-working-location")
	public ResponseEntity<Map<String, Object>> updateWorkingLocation(
			@RequestBody EmpWorkLocationReqDto empWorkLocationReqDto) throws NumberFormatException {
		return ResponseEntity.ok().body(this.employeeService.updateWorkingLocation(empWorkLocationReqDto, true));
	}

	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@PutMapping("/update-shift-timing")
	public ResponseEntity<Map<String, Object>> updateShiftTiming(
			@Valid @RequestBody ShiftTimingReqDto shiftTimingReqDto) throws NumberFormatException {
		if (shiftTimingReqDto.getWeekOff().size() > 2)
			throw new ResourceNotProcessedException(ConstantValues.WEEKOFF_CAN_ONLY_BE_USED_FOR_TWO_DAYS);
		return ResponseEntity.ok().body(this.employeeService.updateShiftTimings(shiftTimingReqDto, true));
	}
	
	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PostMapping("/insert-resigned-emp")
	public ResponseEntity<Map<String, Object>> insertResignedEmp(@RequestBody ResignedEmployee resignedEmployee) {
		return ResponseEntity.ok(this.employeeService.insertResignedEmployee(resignedEmployee));
	}
	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@GetMapping("get-all-resigned-employees")
	public ResponseEntity<Map<String, Object>> getAllResignedEmployees(){
		return ResponseEntity.ok(this.employeeService.getAllResigendEmpData());
		
	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PutMapping("/update-dob")
	public ResponseEntity<Map<String, Object>> updateDobFromExcel(@RequestParam MultipartFile file)
			throws AuthenticationException {

		Map<String, Object> addEmployeeFromExcel = this.employeeService.updateDobFromExcel(file);
		return ResponseEntity.ok(addEmployeeFromExcel);
	}


	@GetMapping("/dob-by-accessLevel")
	public ResponseEntity<Map<String , Object>> getEmpDobByAccessLevel( )
	{
		Map<String,Object> getDob = employeeService.getEmpDobByAccessLevel();
		return ResponseEntity.ok(getDob);
	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PutMapping("/update-resigned-emp")
	public ResponseEntity<Map<String, Object>> updateResignedEmployee(@RequestBody ResignedEmployee resignedEmployee) {
		return ResponseEntity.ok(employeeService.updateResignedEmployee(resignedEmployee));
	}
}
