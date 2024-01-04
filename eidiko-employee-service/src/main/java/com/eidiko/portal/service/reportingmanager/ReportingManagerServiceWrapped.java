package com.eidiko.portal.service.reportingmanager;


import com.eidiko.portal.dto.employee.CautionDto;

import java.util.Map;

public interface ReportingManagerServiceWrapped {
	public Map<String, Object> getReportingEmployees(long managerId, String fromDate, String toDate)throws Exception ;

	public Map<String, Object> getReportingEmployeesDetailedInformation(long empId, long managerId, int year,
			String token)throws Exception ;

	Map<String,Object>getAllEmployeesWithoutManager();
}
