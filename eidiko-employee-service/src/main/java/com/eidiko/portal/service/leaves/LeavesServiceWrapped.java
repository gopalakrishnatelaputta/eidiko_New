package com.eidiko.portal.service.leaves;

import com.eidiko.portal.dto.leaves.EmployeeLeaveStatusDto;
import com.eidiko.portal.dto.leaves.EmployeeLeavesDto;
import com.eidiko.portal.entities.leaves.EmployeeLeaveStatusLeaves;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


public interface LeavesServiceWrapped {

	public Map<String, Object> uploadAttendanceExcelSheet(MultipartFile file, String month, String year,
			long modifiedBy) throws Exception;

	public Map<String, Object> getSpentLeaveCountForEmployees(long empId, int year) throws Exception;

	public Map<String, Object> getEmployeeLeaveStatusReport(int year) throws Exception;

	Map<String, Object> getEmployeeLeaveStatusReportMonthly(int year, int month) throws Exception;

	public Map<String, Object> addLeavesAsPerBand(int year, MultipartFile file, long modifiedBy) throws Exception;

	public Map<String,Object> addLeaves(EmployeeLeaveStatusLeaves employeeLeaveStatusLeaves);

	public Map<String,Object> updateLeaveStatusFields(long empId, EmployeeLeaveStatusDto dto);

	public Map<String,Object> deleteByEmpIdAndLeaveDate(long leaveStatusId);
}
