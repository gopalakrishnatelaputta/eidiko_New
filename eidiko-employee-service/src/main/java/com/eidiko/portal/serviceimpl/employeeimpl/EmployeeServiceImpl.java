package com.eidiko.portal.serviceimpl.employeeimpl;

import com.eidiko.portal.config.employee.SecurityUtil;
import com.eidiko.portal.dto.employee.*;
import com.eidiko.portal.entities.employee.*;
import com.eidiko.portal.entities.leaves.LeavesAsPerBand;
import com.eidiko.portal.exception.employee.PasswordNotValidException;
import com.eidiko.portal.exception.employee.ResourceAlreadyPresentException;
import com.eidiko.portal.exception.employee.ResourceNotFoundException;
import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import com.eidiko.portal.exception.reportingmanager.UserNotFoundException;
import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.helper.employee.Helper;
import com.eidiko.portal.helper.employee.ReadingTemplates;
import com.eidiko.portal.mail.MailService;
import com.eidiko.portal.repo.employee.*;
import com.eidiko.portal.repo.leaves.LeavesAsPerBandRepo;
import com.eidiko.portal.service.employee.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepo employeeRepo;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private DesignationRepo designationRepo;

	@Autowired
	private BandsRepo bandsRepo;

	@Autowired
	private MailService mailService;

	@Autowired
	private ReadingTemplates readingTemplates;

	@Autowired
	private ReportingManagerRepo reportingManagerRepo;

	@Autowired
	private EmpWorkingLocationRepo empWorkingLocationRepo;

	@Autowired
	private EmpAccessLvlRepo accessLvlRepo;

	@Autowired
	private Helper helper;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private EmpShiftTimingRepo empShiftTimingRepo;

	@Autowired
	private EmpAccessLvlMappingRepo accessLvlMappingRepo;

	@Autowired
	private ResignedEmpRepo resignedEmpRepo;

	@Autowired
	private EmpBandDesgMappingRepo bandDesgMappingRepo;
	
	@Autowired
	private LeavesAsPerBandRepo leavesAsPerBandRepo;

	@Value("${files.storage}")
	public String folderLocation;

	private final Path root = Paths.get(folderLocation + ConstantValues.PROFILE_DOCUMENTS_EIDIKO_PORTAL);

	@Override
	public Employee userByUserName(String userName) {
		return this.employeeRepo.findById(Long.parseLong(userName)).orElseThrow(
				() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + userName));
	}

	@Override
	public Map<String, Object> forgotPassword(long empId) {

		Employee employee = this.employeeRepo.findById(empId)
				.orElseThrow(() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));
		Map<String, Object> map = new HashMap<>();

		LoginDetails details = employee.getLoginDetails();
		String password = generateRandomPassword(10, 48, 122);
		if (details != null) {
			details.setPassword(passwordEncoder.encode(password));
		} else {
			LoginDetails loginDetails = new LoginDetails();
			loginDetails.setPassword(passwordEncoder.encode(password));
			loginDetails.setStatus(ConstantValues.ACTIVE_FLAG);
			details = loginDetails;
		}
		details.setEmployee(employee);
		employee.setLoginDetails(details);
		String text = readingTemplates.generateForgotPasswordMailtext(ConstantValues.FORGOT_PASSWORD_MAIL_TEMPLATE_FILE,
				employee.getEmpName(), password);
		EmailDetailsDto detailsDto = new EmailDetailsDto(employee.getEmailId(), text,
				ConstantValues.FORGOT_PASSWORD_MAIL_SUBJECT, null);
		if (this.mailService.sendSimpleMail(detailsDto)) {
			this.employeeRepo.save(employee);
			try {
				map.put(ConstantValues.MESSAGE, ConstantValues.PASSWORD_SENT_TEXT_MAIL);
				map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
				map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK);
				map.put(ConstantValues.EMAIL_ID,employee.getEmailId());
			} catch (Exception e) {
				throw new ResourceNotProcessedException(ConstantValues.PASSWORD_NOT_UPDATED_PLEASE_TRY_AGAIN);
			}
		}

		return map;
	}

	public static String generateRandomPassword(int len, int randNumOrigin, int randNumBound) {
		SecureRandom random = new SecureRandom();
		return random.ints(randNumOrigin, randNumBound + 1)
				.filter(i -> Character.isAlphabetic(i) || Character.isDigit(i)).limit(len)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
	}

	@Override
	public Map<String, Object> createEmployee(Employee employee) throws AuthenticationException {
		Employee emp = this.employeeRepo.findById(employee.getEmpId()).orElse(null);
		if (emp != null) {
			throw new ResourceAlreadyPresentException(
					ConstantValues.EMPLOYEE_IS_ALREADY_PRESENT_WITH_ID + employee.getEmpId());
		}
		

		// Role Setting Default

		Set<EmpRoleMapping> empRoleMappings = new HashSet<>();

		EmpRoleMapping empRoleMapping = new EmpRoleMapping();
		empRoleMapping.setEmployee(employee);
		empRoleMapping.setRoles(this.roleRepo.findById(ConstantValues.EMPLOYEE_ROLE)
				.orElseThrow(() -> new ResourceNotFoundException(ConstantValues.ROLE_NOT_FOUND)));
		empRoleMappings.add(empRoleMapping);
		employee.setEmpRoles(empRoleMappings);
		employee.setDeleted(true);
		employee.setModifiedDate(new Timestamp(System.currentTimeMillis()));
		employee.setStatus(ConstantValues.ACTIVE_FLAG);
		employee.setCreatedBy(Long.parseLong(SecurityUtil.getCurrentUserDetails().getUsername()));
		employee.setAbout(ConstantValues.DEFAULT_ABOUT_TEXT);

		// Default Access Level setting

		EmployeeAccessLevel accessLevel = this.accessLvlRepo.findById(ConstantValues.EMPLOYEE_ACCESS_LEVEL)
				.orElseThrow(() -> new ResourceNotFoundException(ConstantValues.ACCESS_LEVEL_NOT_FOUND));
		EmpAccessLvlMapping accessLvlMapping = new EmpAccessLvlMapping();
		accessLvlMapping.setEmployee(employee);
		accessLvlMapping.setAccessLevel(accessLevel);

		Set<EmpAccessLvlMapping> accessLvlMappings = new HashSet<>();
		accessLvlMappings.add(accessLvlMapping);
		employee.setAccessLvl(accessLvlMappings);

		// inserting data to database
		Map<String, Object> map = new HashMap<>();
		try {
			this.employeeRepo.save(employee);
			for (EmpAccessLvlMapping mapping : accessLvlMappings) {
				this.accessLvlMappingRepo.save(mapping);
			}
			map.put(ConstantValues.MESSAGE, ConstantValues.EMPLOYEE_CREATED_SUCCESS_TEXT);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		} catch (Exception e) {
			throw new ResourceNotProcessedException(ConstantValues.EMPLOYEE_CREATED_FAIL_TEXT);
		}
		return map;
	}

	@Override
	public List<Employee> getAllEmployee(Pageable pageable) {

		Page<Employee> page = this.employeeRepo.findAllByIsDeleted(true, pageable);
		if (page.hasContent()) {
			return page.getContent();
		}

		return new ArrayList<>();
	}

	@Override
	public Map<String, Object> changePassword(ChangePasswordDto changePasswordDto) throws AuthenticationException {

		// Current user Validation with token
		long empId = SecurityUtil.getCurrentUserDetails().getEmpId();

		// New Password and Confirm Password Check
		Map<String, Object> map = new HashMap<>();
		if (!changePasswordDto.getConfirmPassword().equals(changePasswordDto.getNewPassword())) {
			throw new PasswordNotValidException(ConstantValues.NEW_PASSWORD_AND_CONFIRM_PASSWORD_ARE_NOT_MATCHING);
		}

		Employee employee = this.employeeRepo.findById(empId)
				.orElseThrow(() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));

		LoginDetails details = employee.getLoginDetails();

		// Old Password Check
		if (passwordEncoder.matches(changePasswordDto.getOldPassword(), details.getPassword())) {

			details.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
			employee.setLoginDetails(details);

			try {
				this.employeeRepo.save(employee);
				map.put(ConstantValues.MESSAGE, ConstantValues.PASSWORD_UPDATED_TEXT);
				map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
				map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			} catch (Exception e) {
				throw new PasswordNotValidException(ConstantValues.PASSWORD_NOT_UPDATED_PLEASE_TRY_AGAIN);
			}

		} else {
			throw new PasswordNotValidException(ConstantValues.OLD_PASSWORD_IS_NOT_MATCHING);
		}

		return map;
	}

	@Override
	public Map<String, Object> getEmployeeById(long empId) throws AuthenticationException {

		try {
			if (empId == SecurityUtil.getCurrentUserDetails().getEmpId() || this.helper.validateUserOrAuth()) {
				Employee employee = this.employeeRepo.findById(empId).orElseThrow(
						() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));
				Set<ReportingManager> managerForEmployee = employee.getReportingManagerForEmployee();
				Set<ReportingManager> managerForManager = employee.getReportingManagerForManager();

				Set<ReportingManagerDto> managers = new HashSet<>();
				Set<ReportingManagerDto> employees = new HashSet<>();

				for (ReportingManager m : managerForEmployee) {

					if (this.helper.validateCurrentDatePresentOrNot(m.getStartDate(), m.getEndDate())) {
						ReportingManagerDto managerDto = new ReportingManagerDto(m.getReportingManagerId(),
								m.getModifiedDate(), m.getStartDate(), m.getEndDate(), m.getManager().getEmpId(),
								m.getManager().getEmpName());
						managers.add(managerDto);
						break;
					}
				}
				for (ReportingManager m : managerForManager) {
					ReportingManagerDto managerDto = new ReportingManagerDto(m.getReportingManagerId(),
							m.getModifiedDate(), m.getStartDate(), m.getEndDate(), m.getEmployee().getEmpId(),
							m.getEmployee().getEmpName());
					employees.add(managerDto);
				}

				Set<EmpShiftTimings> empShiftTimings = employee.getEmpShiftTimings();
				Set<EmpShiftTimings> empShiftTimingsRes = new HashSet<>();
				for (EmpShiftTimings e : empShiftTimings) {
					if (this.helper.validateCurrentDatePresentOrNot(e.getStartDate(), e.getEndDate())) {
						empShiftTimingsRes.add(e);
						break;
					}

				}

				Set<EmployeeWorkingLocation> employeeWorkingLocations = employee.getEmployeeWorkingLocations();
				Set<EmployeeWorkingLocation> employeeWorkingLocationsRes = new HashSet<>();
				for (EmployeeWorkingLocation wl : employeeWorkingLocations) {
					if (this.helper.validateCurrentDatePresentOrNot(wl.getStartDate(), wl.getEndDate())) {
						employeeWorkingLocationsRes.add(wl);
						break;
					}

				}

				Map<String, Object> map = new HashMap<>();
				map.put(ConstantValues.RESULT, employee);
				map.put(ConstantValues.REPORTING_MANAGERS, managers);
				map.put(ConstantValues.REPORTED_EMPLOYEES, employees);
				map.put("empShiftTimings", empShiftTimingsRes);
				map.put("employeeWorkingLocation", employeeWorkingLocationsRes);
				map.put("designations", employee.getBandDesgMappings());
				map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
				map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());

				return map;

			} else {
				throw new AuthenticationException("You don't have Authentication");
			}

		} catch (AuthenticationException e) {
			throw new AuthenticationException(ConstantValues.SESSION_HAS_BEEN_EXPIRED);
		}

	}

	@Override
	public Map<String, Object> searchEmployeeById(String empName) {

		List<Employee> employees = null;
		try {
			employees = this.employeeRepo.searchEmployee(empName);
		} catch (Exception e) {
			log.error("Exception occurred :", e);
		}
		List<Map<String, Object>> result = new ArrayList<>();

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put(ConstantValues.MESSAGE, ConstantValues.EMPLOYEE_FETCHED_SUCCESSFULLY);
		resultMap.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		resultMap.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());

		for (Employee employee : employees) {
			Map<String, Object> map = new HashMap<>();
			map.put(ConstantValues.EMP_ID, employee.getEmpId());
			map.put(ConstantValues.AUTHORITIES, employee.getAuthorities().stream().map(GrantedAuthority::getAuthority));
			map.put(ConstantValues.DESIGNATION, "");
			map.put(ConstantValues.EMAIL_ID, employee.getEmailId());
			map.put(ConstantValues.USER_NAME, employee.getEmpName());
			result.add(map);
		}
		resultMap.put(ConstantValues.RESULT, result);
		return resultMap;

	}

	@Override
	public Map<String, Object> addShiftTiming(ShiftTimingReqDto shiftTimingReqDto)
			throws NumberFormatException, AuthenticationException {

		long empId = shiftTimingReqDto.getEmpId();
		Employee employee = this.employeeRepo.findById(empId)
				.orElseThrow(() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));
		Set<EmpShiftTimings> timings = employee.getEmpShiftTimings();

		timings.forEach(timing -> {
			if (timing.getEndDate() == null) {
				timing.setEndDate(shiftTimingReqDto.getStartDate());
			}
		});
		EmpShiftTimings empShiftTimings = new EmpShiftTimings();
		empShiftTimings.setStartDate(shiftTimingReqDto.getStartDate());
		empShiftTimings.setEndDate(shiftTimingReqDto.getEndDate());
		empShiftTimings.setShiftStartTime(shiftTimingReqDto.getShiftStartTime());
		empShiftTimings.setShiftEndTime(shiftTimingReqDto.getShiftEndTime());
		empShiftTimings.setEmployee(employee);
		empShiftTimings.setWeekOff(String.join(",", shiftTimingReqDto.getWeekOff()));
		// empShiftTimings.setModifiedBy(SecurityUtil.getCurrentUserDetails().getUsername()
		// + " ("
		// + SecurityUtil.getCurrentUserDetails().getEmpName() + ") ");
		empShiftTimings.setModifiedBy(SecurityUtil.getCurrentUserDetails().getEmpId());
		timings.add(empShiftTimings);
		employee.setEmpShiftTimings(timings);

		try {
			this.employeeRepo.save(employee);
		} catch (Exception e) {
			throw new ResourceNotProcessedException(ConstantValues.NOT_UPDATED);
		}
		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.CREATED.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);
		return map;
	}

	@Override
	public Map<String, Object> addWorkLocation(EmpWorkLocationReqDto empWorkLocationReqDto)
			throws NumberFormatException, AuthenticationException {

		long empId = empWorkLocationReqDto.getEmpId();
		Employee employee = this.employeeRepo.findById(empId)
				.orElseThrow(() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));
		Set<EmployeeWorkingLocation> employeeWorkingLocations = employee.getEmployeeWorkingLocations();
		employeeWorkingLocations.forEach(wl -> {
			if (wl.getEndDate() == null) {
				wl.setEndDate(empWorkLocationReqDto.getStartDate());
			}
		});
		EmployeeWorkingLocation workingLocation = new EmployeeWorkingLocation();
		workingLocation.setLocation(empWorkLocationReqDto.getLocation());
		workingLocation.setWorkingFrom(empWorkLocationReqDto.getWorkingFrom());
		workingLocation.setStartDate(empWorkLocationReqDto.getStartDate());
		workingLocation.setEndDate(empWorkLocationReqDto.getEndDate());
		workingLocation.setModifiedBy(SecurityUtil.getCurrentUserDetails().getEmpId());
		workingLocation.setEmployee(employee);

		employeeWorkingLocations.add(workingLocation);
		employee.setEmployeeWorkingLocations(employeeWorkingLocations);
		try {
			this.employeeRepo.save(employee);
		} catch (Exception e) {
			throw new ResourceNotProcessedException(ConstantValues.NOT_UPDATED);
		}
		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.CREATED.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);
		return map;
	}

	@Override
	public Map<String, Object> addReportingManager(ManagerRequestDto managerRequestDto) throws AuthenticationException {

		long empId = managerRequestDto.getEmpId();

		if (!this.helper.validateStartDateAndEndDate(managerRequestDto.getStartDate(),
				managerRequestDto.getEndDate())) {
			throw new ResourceNotProcessedException("Please enter valid details");
		}

		Employee employee = this.employeeRepo.findById(empId)
				.orElseThrow(() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));
		Employee manager = this.employeeRepo.findById(managerRequestDto.getManagerId())
				.orElseThrow(() -> new ResourceNotFoundException(
						ConstantValues.MANAGER_NOT_FOUND_WITH_ID + managerRequestDto.getManagerId()));

		Set<ReportingManager> reportingManagers = employee.getReportingManagerForEmployee();
		reportingManagers.forEach(rm -> {
			if (rm.getEndDate() == null) {
				rm.setEndDate(managerRequestDto.getStartDate());
			}
		});

		ReportingManager reportingManager = new ReportingManager();

		reportingManager.setManager(manager);
		reportingManager.setStartDate(managerRequestDto.getStartDate());
		reportingManager.setEndDate(managerRequestDto.getEndDate());
		reportingManager.setEmployee(employee);
		reportingManager.setModifiedDate(new Timestamp(System.currentTimeMillis()));
		// reportingManager.setModifiedBy(SecurityUtil.getCurrentUserDetails().getUsername()
		// + "( "
		// + SecurityUtil.getCurrentUserDetails().getEmpName() + " )");

		reportingManager.setModifiedBy(SecurityUtil.getCurrentUserDetails().getEmpId());

		reportingManagers.add(reportingManager);
		employee.setReportingManagerForEmployee(reportingManagers);
		try {
			this.employeeRepo.save(employee);
		} catch (Exception e) {
			log.error("Exception occurred :", e);
			throw new ResourceNotProcessedException(ConstantValues.NOT_UPDATED);
		}
		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.CREATED.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);
		return map;
	}

	@Override
	public Map<String, Object> addAbout(String about) throws AuthenticationException {

		Employee employee = this.employeeRepo
				.findById(Long.parseLong(SecurityUtil.getCurrentUserDetails().getUsername()))
				.orElseThrow(() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID));
		Map<String, Object> map = new HashMap<>();
		employee.setAbout(about);
		try {
			this.employeeRepo.save(employee);
		} catch (Exception e) {
			throw new ResourceNotProcessedException(ConstantValues.NOT_UPDATED);
		}
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);

		return map;
	}

	@Override
	public Map<String, Object> getEmpShiftTimingRecord(long empId) {
		Employee employee = this.employeeRepo.findById(empId)
				.orElseThrow(() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));
		Set<EmpShiftTimings> empShiftTimings = employee.getEmpShiftTimings();
		empShiftTimings.forEach(est -> est.setModifiedByWithName(this.helper.getModifiedByString(est.getModifiedBy())));
		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
		map.put(ConstantValues.RESULT, empShiftTimings);
		map.put(ConstantValues.EMP_ID, empId);
		return map;
	}

	@Override
	public Map<String, Object> getEmpWorkLocationRecord(long empId) {
		Employee employee = this.employeeRepo.findById(empId)
				.orElseThrow(() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));
		Set<EmployeeWorkingLocation> employeeWorkingLocations = employee.getEmployeeWorkingLocations();
		employeeWorkingLocations
				.forEach(ewl -> ewl.setModifiedByWithName(this.helper.getModifiedByString(ewl.getModifiedBy())));
		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
		map.put(ConstantValues.RESULT, employeeWorkingLocations);
		map.put(ConstantValues.EMP_ID, empId);
		return map;
	}

	@Override
	public Map<String, Object> getEmpReportingManagerRecord(long empId) {

		Employee employee = this.employeeRepo.findById(empId)
				.orElseThrow(() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));
		Set<ReportingManager> reportingManagerForEmployee = employee.getReportingManagerForEmployee();
		Set<Map<String, Object>> reportingManagerForEmployeeResult = new HashSet<>();
		for (ReportingManager rm : reportingManagerForEmployee) {
			Map<String, Object> listMap = new HashMap<>();
			listMap.put(ConstantValues.EMP_ID, empId);
			listMap.put(ConstantValues.REPORTING_MANAGER_ID, rm.getManager().getEmpId());
			listMap.put(ConstantValues.REPORTING_MANAGER_NAME, rm.getManager().getEmpName());
			listMap.put("mngrIdWithname", rm.getManager().getEmpId() + " - " + rm.getManager().getEmpName());
			listMap.put(ConstantValues.START_DATE, rm.getStartDate());
			listMap.put(ConstantValues.END_DATE, rm.getEndDate());
			listMap.put(ConstantValues.MODIFIED_BY, this.helper.getModifiedByString(rm.getModifiedBy()));
			listMap.put(ConstantValues.MODIFIED_DATE, rm.getModifiedDate());
			listMap.put("id", rm.getReportingManagerId());

			reportingManagerForEmployeeResult.add(listMap);
		}
		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
		map.put(ConstantValues.RESULT, reportingManagerForEmployeeResult);
		map.put(ConstantValues.EMP_ID, empId);
		return map;
	}

	@Override
	public Map<String, Object> getEmpReportedEmployeeRecord(long empId) {

		Employee employee = this.employeeRepo.findById(empId)
				.orElseThrow(() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));
		Set<ReportingManager> reportingEmployees = employee.getReportingManagerForManager();
		Set<Map<String, Object>> reportingEmployeesResult = new HashSet<>();
		for (ReportingManager rm : reportingEmployees) {
			Map<String, Object> listMap = new HashMap<>();
			listMap.put(ConstantValues.EMP_ID, empId);

			listMap.put(ConstantValues.REPORTING_EMPLOYEE_ID, rm.getEmployee().getEmpId());
			listMap.put(ConstantValues.REPORTING_EMPLOYEE_NAME, rm.getEmployee().getEmpName());
			listMap.put("empIdWithName", rm.getEmployee().getEmpId() + " - " + rm.getEmployee().getEmpName());
			listMap.put(ConstantValues.START_DATE, rm.getStartDate());
			listMap.put(ConstantValues.END_DATE, rm.getEndDate());
			listMap.put(ConstantValues.MODIFIED_BY, this.helper.getModifiedByString(rm.getModifiedBy()));
			listMap.put(ConstantValues.MODIFIED_DATE, rm.getModifiedDate());
			reportingEmployeesResult.add(listMap);
		}
		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
		map.put(ConstantValues.RESULT, reportingEmployeesResult);
		map.put(ConstantValues.EMP_ID, empId);
		map.put("empName", this.helper.getModifiedByString(empId));
		return map;
	}

	@Override
	public Map<String, Object> updateEmployeeContactNo(String contactNo, long empId) {

		Employee employee = this.employeeRepo.findById(empId).orElseThrow(
				() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + ": " + empId));
		employee.setContactNo(contactNo);
		try {
			this.employeeRepo.save(employee);
		} catch (Exception e) {
			log.error("Exception occurred :", e);
			throw new ResourceNotProcessedException(ConstantValues.NOT_PROCESSED);
		}
		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.CONTACT_UPDATED_SUCCESSFULLY);
		return map;
	}

	@Override
	public Map<String, Object> updateEmployee(EmployeeDto employeeDto) throws AuthenticationException {
		Employee employee = this.employeeRepo.findById(employeeDto.getEmpId())
				.orElseThrow(() -> new ResourceNotFoundException(
						ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + employeeDto.getEmpId()));
		employee.setEmpId(employeeDto.getEmpId());
		employee.setEmpName(employeeDto.getEmpName());
		employee.setEmailId(employeeDto.getEmailId());
		employee.setDateOfJoining(employeeDto.getDateOfJoining());
		employee.setContactNo(employeeDto.getContactNo());
		employee.setDateOfBirth(employeeDto.getDateOfBirth());

		Map<String, Object> map = new HashMap<>();
		try {
			this.employeeRepo.save(employee);
			
			if(employeeDto.getDesignation() != null && employeeDto.getBand() !=0 ) {
			Designations designationName = designationRepo.findByDesignationName(employeeDto.getDesignation());
			EmployeeBandDesgMapping bandDesgMappings = bandDesgMappingRepo.findByEmployeeEmpId(employeeDto.getEmpId());
			Bands bands = bandsRepo.findById(employeeDto.getBand()).orElseThrow();
			EmployeeBandDesgMapping bandDesgMapping = new EmployeeBandDesgMapping();
			//
			int currentYear=LocalDate.now().getYear();
			LeavesAsPerBand leavesAsPerBand1=leavesAsPerBandRepo.findByEmpIdAndYear(employeeDto.getEmpId(),currentYear);

			// Record for the current year already exists, update it
				if (leavesAsPerBand1 != null) {
					leavesAsPerBand1.setBand("Band " + employeeDto.getBand());
					leavesAsPerBand1.setLeaves(employeeDto.getEligibleLeaves());
					leavesAsPerBandRepo.save(leavesAsPerBand1);
				}
				else
				// Record for the current year doesn't exist, create a new one
				{
					LeavesAsPerBand leavesAsPerBand = new LeavesAsPerBand();
					leavesAsPerBand.setBand("Band " + employeeDto.getBand());
					leavesAsPerBand.setEmpId(employeeDto.getEmpId());
					leavesAsPerBand.setYear(currentYear);
					leavesAsPerBand.setLeaves(employeeDto.getEligibleLeaves());
					leavesAsPerBandRepo.save(leavesAsPerBand);
				}
			//
			if(bandDesgMappings == null ) {
				
				bandDesgMapping.setBand(bands);
				bandDesgMapping.setDesignation(designationName);
				bandDesgMapping.setEffectiveDate(employee.getDateOfJoining());
				bandDesgMapping.setEmployee(employee);
				bandDesgMapping.setModifiedBy(SecurityUtil.getCurrentUserDetails().getEmpId());
				bandDesgMapping.setModifiedDate(new Timestamp(System.currentTimeMillis()));
				bandDesgMappingRepo.save(bandDesgMapping);	
			}else {
			

			bandDesgMappings.setDesignation(designationName);
			bandDesgMappings.setBand(bands);
			bandDesgMappings.setModifiedDate(new Timestamp(System.currentTimeMillis()));
			
			bandDesgMappingRepo.save(bandDesgMappings);
			}
			}


		} catch (Exception e) {
			throw new ResourceNotProcessedException(ConstantValues.NOT_UPDATED);
		}
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);

		return map;

	}


	public Map<String, Object> deleteEmployee(long empId) throws AuthenticationException {
		Employee employee = this.employeeRepo.findById(empId)
				.orElseThrow(() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));
		Map<String, Object> map = new HashMap<>();
		try {
			employee.setDeleted(false);
			employee.setStatus(ConstantValues.INACTIVE_FLAG);
			employeeRepo.save(employee);
		} catch (Exception e) {
			throw new ResourceNotProcessedException(ConstantValues.NOT_DELETED);
		}
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);

		return map;
	}

	@Override
	public Map<String, Object> updateProfilePic(MultipartFile file, long empId) throws AuthenticationException {
		try {

			Employee user = this.employeeRepo.findById(empId)
					.orElseThrow(() -> new Exception(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));
			String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
			ArrayList<String> list = new ArrayList<>();
			list.add("jpeg");
			list.add("jpg");
			list.add("png");
			if (!list.contains(fileExtension)) {
				throw new ResourceNotProcessedException("please provide image in JPEG or png format");
			}

			String fileName = empId + "_" + user.getEmailId() + "." + fileExtension;

			EmpProfilePic profilePic = user.getEmpProfilePic();
			if (profilePic != null) {
				profilePic.setEmpProfile(fileName);
			} else {
				profilePic = new EmpProfilePic();
				profilePic.setEmpProfile(fileName);
			}
			profilePic.setEmployee(user);

			user.setEmpProfilePic(profilePic);
			Files.copy(file.getInputStream(),
					Paths.get(folderLocation + ConstantValues.PROFILE_DOCUMENTS_EIDIKO_PORTAL).resolve(fileName),
					StandardCopyOption.REPLACE_EXISTING);
			this.employeeRepo.save(user);
		} catch (Exception e) {
			if (e instanceof FileAlreadyExistsException) {
				throw new ResourceNotProcessedException("A file of that name already exists.");
			}

			throw new ResourceNotProcessedException(e.getMessage());
		}
		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);
		return map;

	}

	@Override
	public Resource loadProfile(long empId) {

		Employee employee = this.employeeRepo.findById(empId)
				.orElseThrow(() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID));
		String filename = null;
		EmpProfilePic profilePic = employee.getEmpProfilePic();
		if (profilePic != null) {
			filename = profilePic.getEmpProfile();
		}
		if (filename == null || filename.equals("")) {
			filename = "default.png";
		}
		try {
			Path file = Paths.get(folderLocation + ConstantValues.PROFILE_DOCUMENTS_EIDIKO_PORTAL).resolve(filename);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new ResourceNotProcessedException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new ResourceNotProcessedException("Error: " + e.getMessage());
		}
	}

	@Override
	public Map<String, Object> addEmployeeFromExcel(MultipartFile file) throws AuthenticationException {

		String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
		ArrayList<String> list = new ArrayList<>();
		list.add("xls");
		list.add("xlsx");
		list.add("csv");
		if (!list.contains(fileExtension)) {
			throw new ResourceNotProcessedException("please provide excel file");
		}

		Map<String, Object> map = new HashMap<>();
		List<EmployeeDto> employeeDtos = new ArrayList<>();
		List<ManagerRequestDto> managerRequestDtos = new ArrayList<>();
		List<ShiftTimingReqDto> shiftTimingReqDtos = new ArrayList<>();
		List<EmpWorkLocationReqDto> empWorkLocationReqDtos = new ArrayList<>();
		List<EmployeeBandDesgReqDto> bandDesgReqDtos = new ArrayList<>();

		// Read Excel and create List of EmployeeDto Object

		try {
			XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
			XSSFSheet sheet = workbook.getSheetAt(0);

			for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
				XSSFRow row = sheet.getRow(i);
				long empId = (long) row.getCell(0).getNumericCellValue();
				String empName = row.getCell(1).getStringCellValue();
				String emailId = row.getCell(2).getStringCellValue();
				long reportingManager = (long) row.getCell(3).getNumericCellValue();
				Date doj = row.getCell(4).getDateCellValue();
				java.sql.Date dateOfJoining = new java.sql.Date(doj.getTime());
				String status = row.getCell(5).getStringCellValue();
				int roleId = (int) row.getCell(6).getNumericCellValue();
				String workStatus = row.getCell(7).getStringCellValue();

				DataFormatter df = new DataFormatter();
				String shiftStartTime = df.formatCellValue(row.getCell(8));
				String shiftEndTime = df.formatCellValue(row.getCell(9));
				String Designation = row.getCell(10).getStringCellValue();
				String location = row.getCell(11).getStringCellValue();
				String band = row.getCell(12).getStringCellValue();

				log.info(empId + "------------" + Designation + ">><<<" + location);

				EmployeeDto employeeDto = new EmployeeDto();
				employeeDto.setEmailId(emailId);
				employeeDto.setEmpId(empId);
				employeeDto.setEmpName(empName);
				employeeDto.setDateOfJoining(dateOfJoining);
				employeeDtos.add(employeeDto);

				ManagerRequestDto managerRequestDto = new ManagerRequestDto();
				managerRequestDto.setEmpId(empId);
				managerRequestDto.setManagerId(reportingManager);
				managerRequestDto.setStartDate(this.helper.getStartDate(dateOfJoining));
				managerRequestDtos.add(managerRequestDto);

				EmpWorkLocationReqDto empWorkLocationReqDto = new EmpWorkLocationReqDto();
				empWorkLocationReqDto.setWorkingFrom(workStatus);
				empWorkLocationReqDto.setEmpId(empId);
				empWorkLocationReqDto.setStartDate(this.helper.getStartDate(dateOfJoining));
				empWorkLocationReqDto.setLocation(location);
				empWorkLocationReqDtos.add(empWorkLocationReqDto);

				ShiftTimingReqDto shiftTimingReqDto = new ShiftTimingReqDto();
				log.info(shiftStartTime + "<<<<<<>>>>>>" + shiftEndTime);
				shiftTimingReqDto.setEmpId(empId);
				if (workStatus.trim().equals("Client location") || workStatus.trim().equals("WFH")) {

					shiftTimingReqDto.setShiftStartTime(Time.valueOf("10:00:00"));
					shiftTimingReqDto.setShiftEndTime(Time.valueOf("19:00:00"));
				} else if ("".equals(shiftStartTime) || "".equals(shiftEndTime)) {

					shiftTimingReqDto.setShiftStartTime(Time.valueOf("10:00:00"));
					shiftTimingReqDto.setShiftEndTime(Time.valueOf("19:00:00"));
				} else {

					shiftTimingReqDto.setShiftStartTime(Time.valueOf(shiftStartTime + ":00"));
					shiftTimingReqDto.setShiftEndTime(Time.valueOf(shiftEndTime + ":00"));
				}
				shiftTimingReqDto.setStartDate(this.helper.getStartDate(dateOfJoining));

				String[] weekoff = { "1", "7" };
				shiftTimingReqDto.setWeekOff(Arrays.asList(weekoff));
				shiftTimingReqDtos.add(shiftTimingReqDto);

				EmployeeBandDesgReqDto bandDesgReqDto = new EmployeeBandDesgReqDto();
				bandDesgReqDto.setEmpId(empId);
				bandDesgReqDto.setBandId(Integer.parseInt(band.trim().split(" ")[1]));
				bandDesgReqDto.setDesgName(Designation);

				bandDesgReqDtos.add(bandDesgReqDto);

			}

			try {
				// saveAllEmployeesFromList(employeeDtos);
				saveAllEmployeesReportingmanagerFromList(managerRequestDtos);
				saveAllEmployeesWorkLocationFromList(empWorkLocationReqDtos);
				saveAllEmployeesShiftTimingFromList(shiftTimingReqDtos);
//				saveAllEmployeesDesignationAndBand(bandDesgReqDtos);

			} catch (Exception e) {
				throw new ResourceNotProcessedException(e.getMessage());
			}

			map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);

		} catch (IOException e) {
			log.error("Exception occurred :", e);
		}

		return map;
	}

	@Override
	public Map<String, Object> updateReportingManager(ManagerRequestDto managerRequestDto, boolean isEmpController) {

		ReportingManager reportingManager = this.reportingManagerRepo
				.findById(managerRequestDto.getReportingManagerId())
				.orElseThrow(() -> new ResourceNotFoundException("please enter valid reporting manager id"));
		if (reportingManager.getEmployee().getEmpId() == managerRequestDto.getManagerId()) {
			throw new ResourceNotProcessedException(ConstantValues.EMPLOYEE_AND_MANAGER_SHOULD_NOT_BE_THE_SAME);
		}

		Employee manager = this.employeeRepo.findById(managerRequestDto.getManagerId())
				.orElseThrow(() -> new ResourceNotFoundException(
						ConstantValues.MANAGER_NOT_FOUND_WITH_ID + managerRequestDto.getManagerId()));

		Employee currentUserDetails;
		try {
			currentUserDetails = SecurityUtil.getCurrentUserDetails();
			if (isEmpController
					&& (!Objects.equals(reportingManager.getEmployee().getEmpId(), currentUserDetails.getEmpId()))) {
				throw new ResourceNotProcessedException(ConstantValues.NOT_PROCESSED);

			}

		} catch (AuthenticationException e) {
			throw new ResourceNotProcessedException(ConstantValues.SESSION_HAS_BEEN_EXPIRED);
		}

		reportingManager.setManager(manager);
		reportingManager.setEndDate(managerRequestDto.getEndDate());
		reportingManager.setStartDate(managerRequestDto.getStartDate());
		// reportingManager.setModifiedBy(currentUserDetails.getEmpId() + " (" +
		// currentUserDetails.getEmpName() + " )");
		reportingManager.setModifiedBy(currentUserDetails.getEmpId());
		reportingManager.setModifiedDate(new Timestamp(System.currentTimeMillis()));
		Map<String, Object> map = new HashMap<>();
		try {
			this.reportingManagerRepo.save(reportingManager);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);
		} catch (RuntimeException e) {
			throw new ResourceNotProcessedException(e.getMessage());
		}

		return map;
	}

	@Override
	public Map<String, Object> updateWorkingLocation(EmpWorkLocationReqDto empWorkLocationReqDto,
			boolean isEmpController) {
		EmployeeWorkingLocation workingLocation = this.empWorkingLocationRepo
				.findById(empWorkLocationReqDto.getEmpWorkLocationId())
				.orElseThrow(() -> new ResourceNotFoundException("Please enter valid work locatio id"));
		workingLocation.setEndDate(empWorkLocationReqDto.getEndDate());
		workingLocation.setStartDate(empWorkLocationReqDto.getStartDate());
		workingLocation.setWorkingFrom(empWorkLocationReqDto.getWorkingFrom());
		workingLocation.setLocation(empWorkLocationReqDto.getLocation());
		Employee currentUserDetails;
		try {
			currentUserDetails = SecurityUtil.getCurrentUserDetails();
			if (isEmpController
					&& (!Objects.equals(workingLocation.getEmployee().getEmpId(), currentUserDetails.getEmpId()))) {
				throw new ResourceNotProcessedException(ConstantValues.NOT_PROCESSED);

			}
		} catch (AuthenticationException e) {
			throw new ResourceNotProcessedException(ConstantValues.SESSION_HAS_BEEN_EXPIRED);

		}
		workingLocation.setModifiedBy(currentUserDetails.getEmpId());
		Map<String, Object> map = new HashMap<>();
		try {
			this.empWorkingLocationRepo.save(workingLocation);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);
		} catch (RuntimeException e) {
			throw new ResourceNotProcessedException(e.getMessage());
		}

		return map;
	}

	@Override
	public Map<String, Object> updateShiftTimings(ShiftTimingReqDto shiftTimingReqDto, boolean isEmpController) {

		EmpShiftTimings shiftTimings = this.empShiftTimingRepo.findById(shiftTimingReqDto.getShiftTimingId())
				.orElseThrow(() -> new ResourceNotFoundException("Please enter valid shift timing id"));
		shiftTimings.setEndDate(shiftTimingReqDto.getEndDate());
		shiftTimings.setStartDate(shiftTimingReqDto.getStartDate());
		shiftTimings.setShiftStartTime(shiftTimingReqDto.getShiftStartTime());
		shiftTimings.setShiftEndTime(shiftTimingReqDto.getShiftEndTime());
		shiftTimings.setWeekOff(String.join(",", shiftTimingReqDto.getWeekOff()));

		Employee currentUserDetails;
		try {
			currentUserDetails = SecurityUtil.getCurrentUserDetails();
			if (isEmpController
					&& (!Objects.equals(shiftTimings.getEmployee().getEmpId(), currentUserDetails.getEmpId()))) {
				throw new ResourceNotProcessedException(ConstantValues.NOT_PROCESSED);

			}
		} catch (AuthenticationException e) {
			throw new ResourceNotProcessedException(ConstantValues.SESSION_HAS_BEEN_EXPIRED);
		}

		shiftTimings.setModifiedBy(currentUserDetails.getEmpId());
		Map<String, Object> map = new HashMap<>();
		try {
			this.empShiftTimingRepo.save(shiftTimings);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);
		} catch (RuntimeException e) {
			throw new ResourceNotProcessedException(e.getMessage());
		}

		return map;
	}

	public boolean saveAllEmployeesFromList(List<EmployeeDto> employeeDtos) throws AuthenticationException {

		List<Employee> all = this.employeeRepo.findAll();
		List<Long> empIds = new ArrayList<>();

		all.forEach(e -> empIds.add(e.getEmpId()));

		for (EmployeeDto e : employeeDtos) {
			if (empIds.contains(e.getEmpId())) {
				continue;
			}
			createEmployee(this.modelMapper.map(e, Employee.class));
		}
		return true;
	}

	public boolean saveAllEmployeesReportingmanagerFromList(List<ManagerRequestDto> managerRequestDtos)
			throws AuthenticationException {
		for (ManagerRequestDto e : managerRequestDtos) {
			addReportingManager(e);
		}
		return true;
	}

	public boolean saveAllEmployeesWorkLocationFromList(List<EmpWorkLocationReqDto> empWorkLocationReqDtos)
			throws AuthenticationException {

		for (EmpWorkLocationReqDto e : empWorkLocationReqDtos) {
			addWorkLocation(e);
		}
		return true;
	}

	public boolean saveAllEmployeesShiftTimingFromList(List<ShiftTimingReqDto> shiftTimingReqDtos)
			throws AuthenticationException {

		for (ShiftTimingReqDto e : shiftTimingReqDtos) {
			addShiftTiming(e);
		}
		return true;
	}

	public boolean saveAllEmployeesDesignationAndBand(List<EmployeeBandDesgReqDto> bandDesgReqDtos)
			throws AuthenticationException {

		List<Employee> employees = new ArrayList<>();

		for (EmployeeBandDesgReqDto e : bandDesgReqDtos) {
			Employee employee = this.employeeRepo.findById(e.getEmpId()).get();

			Set<EmployeeBandDesgMapping> bandDesgMappings = new HashSet<>();
			EmployeeBandDesgMapping bandDesgMapping = new EmployeeBandDesgMapping();

			Bands bands = new Bands();
			bands.setBandId(e.getBandId());

			bands = this.bandsRepo.save(bands);

			bandDesgMapping.setBand(bands);
			Designations designations = this.designationRepo.findByDesignationName(e.getDesgName().trim());
			if (designations == null) {

				Designations desg = new Designations();
				desg.setDesignationName(e.getDesgName().trim());
				desg = this.designationRepo.save(desg);
				designations = desg;
			}
			bandDesgMapping.setDesignation(designations);
			bandDesgMapping.setEffectiveDate(this.helper.getStartDate(employee.getDateOfJoining()));
			bandDesgMapping.setModifiedBy(SecurityUtil.getCurrentUserDetails().getEmpId());
			bandDesgMapping.setModifiedDate(new Timestamp(System.currentTimeMillis()));
			bandDesgMapping.setEmployee(employee);
			bandDesgMappings.add(bandDesgMapping);

			employee.setBandDesgMappings(bandDesgMappings);

			employees.add(employee);
		}

		try {
			this.employeeRepo.saveAll(employees);
		} catch (Exception ex) {
			log.error("Exception occurred :", ex);
		}

		return true;
	}

	public Map<String, Object> addAccessLevel(long empId, int accessLvlId) {

		Map<String, Object> map = new HashMap<>();
		Employee employee = employeeRepo.findById(empId).orElseThrow(
				() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + " " + empId));
		EmployeeAccessLevel employeeAccessLevel = accessLvlRepo.findById(accessLvlId).orElseThrow(
				() -> new ResourceNotFoundException(ConstantValues.ACCESS_LEVEL_NOT_FOUND + " " + accessLvlId));
		EmpAccessLvlMapping accessLvlMapping = accessLvlMappingRepo.findByEmployeeEmpIdAndAccessLevelAccessLvlId(empId,
				accessLvlId);

		EmpAccessLvlMapping empAccessLvlMapping = new EmpAccessLvlMapping();

		if (accessLvlMapping == null) {
			empAccessLvlMapping.setAccessLevel(employeeAccessLevel);
			empAccessLvlMapping.setEmployee(employee);
			this.accessLvlMappingRepo.save(empAccessLvlMapping);

		} else {
			throw new ResourceNotFoundException(ConstantValues.ACCESS_LEVEL_IS_ALREADY_PRESENT);
		}
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);
		return map;
	}

	public Map<String, Object> deleteEmpAccess(long accessLvlId, long empId) {
		Map<String, Object> map = new HashMap<>();
		Set<EmpAccessLvlMapping> accessLvlMappings = new HashSet<>();
		try {
			Employee employee = this.employeeRepo.findById(empId).orElseThrow(
					() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));
			Set<EmpAccessLvlMapping> accessLvl = employee.getAccessLvl();
			accessLvl.forEach(a -> {

				if (a.getAccessLevel().getAccessLvlId() == accessLvlId) {
					accessLvlMappings.add(a);
				}

			});

			this.accessLvlMappingRepo.deleteAll(accessLvlMappings);

		} catch (Exception e) {
			log.error("Exception occurred :", e);
			throw new ResourceNotProcessedException("Access Level not updated");
		}

		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);
		return map;
	}

	@Override
	public Map<String, Object> getEmpByAccessLvl(long accessLvlId) {
		List<Employee> employeeByAccessLvl = new ArrayList<>();
		if (accessLvlId == 0000) {
			employeeByAccessLvl = this.employeeRepo.findAll();
		} else {
			employeeByAccessLvl = this.employeeRepo.getEmployeeByAccessLvl(accessLvlId);
		}
		try {
			List<ResignedEmployee> resgnEmpList = resignedEmpRepo.findAll();
			Set<Long> resignedEmpIds = resgnEmpList.stream().map(ResignedEmployee::getEmpId)
					.collect(Collectors.toSet());
			List<Employee> filteredEmployees = employeeByAccessLvl.stream()
					.filter(employee -> !resignedEmpIds.contains(employee.getEmpId())).collect(Collectors.toList());
			Map<String, Object> map = new HashMap<>();
			map.put(ConstantValues.RESULT, filteredEmployees);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);

			return map;
		} catch (Exception e) {
			log.error("Exception occurred :", e);
			throw new ResourceNotProcessedException("Data not found");
		}

	}

	@Override
	public Map<String, Object> getEmpByWorkLocation(String workingfrom) {

		List<EmployeeWorkingLocation> list = this.empWorkingLocationRepo.findAllByWorkingFrom(workingfrom);
		if (workingfrom.equalsIgnoreCase(ConstantValues.CLIENT_LOCATION)) {
			list = this.empWorkingLocationRepo.findAllByWorkingFromClientLocation(ConstantValues.CLIENT_LOCATION,
					ConstantValues.CLIENT_LOCATION2);
		}
		List<EmployeeDto> empList = new ArrayList<>();
		list.forEach(l -> {
			java.sql.Date startDate = l.getStartDate();
			java.sql.Date endDate = l.getEndDate();
			java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
			if ((DateUtils.isSameDay(currentDate, startDate) || startDate.before(currentDate))
					&& (endDate == null || DateUtils.isSameDay(currentDate, endDate) || endDate.after(currentDate))) {
				Employee employee = l.getEmployee();
				EmployeeDto dto = new EmployeeDto();
				dto.setEmpId(employee.getEmpId());
				dto.setEmpName(employee.getEmpName());
				dto.setContactNo(employee.getContactNo());
				dto.setDateOfJoining(employee.getDateOfJoining());
				dto.setEmailId(employee.getEmailId());
				dto.setWorkingfrom(l.getWorkingFrom());
				if (l.getWorkingFrom().equalsIgnoreCase(ConstantValues.CLIENT_LOCATION)
						|| l.getWorkingFrom().equalsIgnoreCase(ConstantValues.CLIENT_LOCATION2)) {
					dto.setLocation(l.getLocation());
				}
				empList.add(dto);
			}

		});
		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.RESULT, empList);
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);
		return map;
	}

	@Override
	public Map<String, Object> insertResignedEmployee(ResignedEmployee resignedEmployee) {
		Map<String, Object> map = new HashMap<>();
		long empId = resignedEmployee.getEmpId();
		boolean existsById = employeeRepo.existsById(empId);
		if (existsById) {
			boolean existsByEmpId = resignedEmpRepo.existsByempId(resignedEmployee.getEmpId());
			if (!existsByEmpId) {
				ResignedEmployee data = resignedEmpRepo.save(resignedEmployee);
                Employee employee = employeeRepo.findById(data.getEmpId()).orElseThrow();
                employee.setDeleted(false);
				employee.setStatus(ConstantValues.INACTIVE_FLAG);
                employeeRepo.save(employee);
                map.put(ConstantValues.RESULT, "Data Inserted");
				map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
				map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
				map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);
				return map;
			}
			map.put(ConstantValues.RESULT, "EMPID : " + empId + " Already added to Resigned Table");
			map.put(ConstantValues.STATUS_CODE, "Failed");
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.BAD_REQUEST);
			return map;
		} else
			map.put(ConstantValues.RESULT, "EMPID : " + empId + " does not Exists");
		map.put(ConstantValues.STATUS_CODE, "Failed");
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.BAD_REQUEST);
		return map;
	}

	@Override
	public Map<String, Object> getAllResigendEmpData() {
		Map<String, Object> map = new HashMap<>();
		List<ResignedEmployee> resgnEmployees = resignedEmpRepo.findAll();
		List<ResignedEmployee> resgnEmp = new ArrayList<>();
		for (ResignedEmployee resignedEmployee : resgnEmployees) {
			long empId = resignedEmployee.getEmpId();
			List<Employee> employeeList = employeeRepo.findByEmpId(empId);
			if(employeeList != null) {
				String empName = employeeList.get(0).getEmpName();
				resignedEmployee.setEmpName(empName);
			}
			resgnEmp.add(resignedEmployee);
		}
		map.put(ConstantValues.RESULT, resgnEmp);
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK);
		return map;
	}

	@Override
	public Map<String, Object> updateDobFromExcel(MultipartFile file) {
		Map<String,Object> map=new HashMap<>();
		String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
		ArrayList<String> list = new ArrayList<>();
		list.add("xls");
		list.add("xlsx");
		list.add("csv");
		if (!list.contains(fileExtension)) {
			throw new ResourceNotProcessedException("please provide excel file");
		}

		try {
			XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
			XSSFSheet sheet = workbook.getSheetAt(0);

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				XSSFRow row = sheet.getRow(i);
				if (row != null) {
					XSSFCell empIdCell = row.getCell(0);
					XSSFCell dobCell = row.getCell(1);
					if (empIdCell != null && dobCell != null) {
						long empId = (long) empIdCell.getNumericCellValue();
						if (dobCell.getCellType() == CellType.NUMERIC) {
							Date dob = dobCell.getDateCellValue();
							java.sql.Date dateOfBirth = new java.sql.Date(dob.getTime());
							Employee employee = employeeRepo.findById(empId)
									.orElseThrow(() -> new UserNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));
							if (employee != null) {
								employee.setDateOfBirth(dateOfBirth.toLocalDate());
								employeeRepo.save(employee);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.UPDATED);
		return map;
	}



	@Override
	public Map<String, Object> getEmpDobByAccessLevel() {
		Map<String, Object> map = new HashMap<>();
		LocalDate currentDate = LocalDate.now();
		List<Employee> employee=this.employeeRepo.findAllByIsDeleted(true);
		int accessLevel=1001;
		List<EmpAccessLvlMapping> accessLvlMapping = this.accessLvlMappingRepo.findByAccessLevelAccessLvlId(accessLevel);

		List<DateOfBirthDto> list = new ArrayList<>();
		if(accessLvlMapping!=null){
			for(EmpAccessLvlMapping e:accessLvlMapping){
				for(Employee employee1:employee){
					if(Objects.equals(employee1.getEmpId(), e.getEmployee().getEmpId())){
						DateOfBirthDto date = new DateOfBirthDto();
						if((employee1.getDateOfBirth()!=null) && (currentDate.getMonthValue() == employee1.getDateOfBirth().getMonthValue()
								&& currentDate.getDayOfMonth() == employee1.getDateOfBirth().getDayOfMonth())){
							date.setEmpId(employee1.getEmpId());
							date.setEmpName(employee1.getEmpName());
							date.setDateOfBirth(employee1.getDateOfBirth());
							list.add(date);
						}
					}
				}
			}
		}
		map.put(ConstantValues.RESULT, list);
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
		return map;
	}

	@Override
	public Map<String, Object> updateResignedEmployee(ResignedEmployee resignedEmployee) {
		Map<String, Object> map = new HashMap<>();
		long resignedId = resignedEmployee.getResignedId();
		ResignedEmployee employee = this.resignedEmpRepo.findById(resignedId).orElseThrow(()->
				new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID+resignedEmployee.getEmpId()));
		if (employee != null) {
			employee.setStartDate(resignedEmployee.getStartDate());
			employee.setEndDate(resignedEmployee.getEndDate());
			employee.setModifiedBy(resignedEmployee.getModifiedBy());
			employee.setModifiedDate(new Timestamp(System.currentTimeMillis()));
			this.resignedEmpRepo.save(employee);
		}else {
			throw new ResourceNotProcessedException(ConstantValues.NOT_PROCESSED);
		}
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);
		return map;
	}

}
