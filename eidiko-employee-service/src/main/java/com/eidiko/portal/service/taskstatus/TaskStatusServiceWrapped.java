package com.eidiko.portal.service.taskstatus;

import com.eidiko.portal.dto.taskstatus.DailyStatusReportDto;
import com.eidiko.portal.exception.taskstatus.NotNullException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.data.domain.Pageable;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface TaskStatusServiceWrapped {

	
	public Map<String, Object> getDailyStatusReportFromDateToDate(Long empId, Timestamp startDate, Timestamp endDate, Pageable pageable) throws JsonMappingException, JsonProcessingException;
	
	
	public void addTask(DailyStatusReportDto dailyStatusReportDto, long empId) throws URISyntaxException, JsonMappingException, JsonProcessingException, NotNullException;


	public void updateVerifiedBy(List<Long> taskDetailsId, long verifiedById);
	
	
	public Map<String,Object> getAllDailyStatusReports(Pageable pageable) ;


	public Map<String,Object> getAllReportsByEmpId(long empId, Pageable pageable) ;


	public Map<String,Object> getAllReportsGivenDates(Timestamp fromDate, Timestamp toDate, Pageable pageable);

	public Map<String,Object> getPendingReports(Timestamp fromDate, Timestamp toDate);

	public Map<String,Object> getStatusReport(Timestamp fromDate, Timestamp toDate, String status, Pageable pageable);

	public Map<String,Object> getAllPendingStatus(String status, Pageable pageable);

	public Map<String,Object> getAllPendingReports();
	
}
