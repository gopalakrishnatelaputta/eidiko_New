package com.eidiko.portal.service.employee;

import com.eidiko.portal.dto.employee.*;
import com.eidiko.portal.entities.employee.Employee;
import com.eidiko.portal.entities.employee.ResignedEmployee;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.security.sasl.AuthenticationException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface EmployeeService {

	public Employee userByUserName(String userName);

	public Map<String, Object> forgotPassword(long empId);

	public Map<String, Object> createEmployee(Employee employee) throws AuthenticationException;

	public List<Employee> getAllEmployee(Pageable pageable);

	public Map<String, Object> changePassword(ChangePasswordDto changePasswordDto) throws AuthenticationException;

	public Map<String, Object> getEmployeeById(long empId) throws AuthenticationException;

	public Map<String, Object> searchEmployeeById(String empId);

	public Map<String, Object> addShiftTiming(ShiftTimingReqDto shiftTimingReqDto)
			throws NumberFormatException, AuthenticationException;

	public Map<String, Object> addWorkLocation(EmpWorkLocationReqDto empWorkLocationReqDto)
			throws NumberFormatException, AuthenticationException;

	public Map<String, Object> addReportingManager(ManagerRequestDto managerRequestDto) throws AuthenticationException;

	public Map<String, Object> addAbout(String about) throws AuthenticationException;

	public Map<String, Object> getEmpShiftTimingRecord(long empId);

	public Map<String, Object> getEmpWorkLocationRecord(long empId);

	public Map<String, Object> getEmpReportingManagerRecord(long empId);

	public Map<String, Object> getEmpReportedEmployeeRecord(long empId);

	public Map<String, Object> updateEmployeeContactNo(String contactNo, long empId);

	public Map<String, Object> updateEmployee(EmployeeDto employeeDto) throws AuthenticationException;

	public Map<String, Object> deleteEmployee(long empId) throws AuthenticationException;

	public Map<String, Object> updateProfilePic(MultipartFile file, long empId) throws AuthenticationException;

	Resource loadProfile(long empId);

	public Map<String, Object> addEmployeeFromExcel(MultipartFile file) throws AuthenticationException;

	public Map<String, Object> updateReportingManager(ManagerRequestDto managerRequestDto, boolean isEmpController);

	public Map<String, Object> updateWorkingLocation(EmpWorkLocationReqDto empWorkLocationReqDto,
			boolean isEmpController);

	public Map<String, Object> updateShiftTimings(ShiftTimingReqDto shiftTimingReqDto, boolean isEmpController);

	public Map<String, Object> addAccessLevel(long empId, int accessLvlId);

	public Map<String, Object> deleteEmpAccess(long accessLvlId, long empId);

	public Map<String, Object> getEmpByAccessLvl(long accessLvlId);
	
	public Map<String, Object> getEmpByWorkLocation(String workingfrom);

	
	public Map<String , Object> insertResignedEmployee(ResignedEmployee resignedEmployee); 
	
	public Map<String, Object>  getAllResigendEmpData();

	Map<String, Object> updateDobFromExcel(MultipartFile file);

	Map<String, Object> getEmpDobByAccessLevel();

	Map<String, Object> updateResignedEmployee(ResignedEmployee resignedEmployee);
}
