package com.eidiko.portal.serviceimpl.taskstatusimpl;

import com.eidiko.portal.dto.taskstatus.DailyStatusReportDto;
import com.eidiko.portal.entities.taskstatus.DailyStatusReport;
import com.eidiko.portal.entities.taskstatus.EmployeeStatusReport;
import com.eidiko.portal.entities.taskstatus.TaskStatusExemptionEmp;
import com.eidiko.portal.exception.taskstatus.*;
import com.eidiko.portal.helper.taskstatus.ConstantValues;
import com.eidiko.portal.helper.taskstatus.Loop;
import com.eidiko.portal.repo.taskstatus.DailyStatusRepository;
import com.eidiko.portal.repo.taskstatus.EmployeeRepository;
import com.eidiko.portal.repo.taskstatus.TaskStatusExemptionRepository;
import com.eidiko.portal.service.taskstatus.TaskStatusServiceWrapped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TaskStatusServiceImplWrapped implements TaskStatusServiceWrapped {

	@Autowired
	private DailyStatusRepository dailyStatusRepository;

	@Autowired
	private EmployeeRepository employeeRepository;


	@Autowired
	private TaskStatusExemptionRepository taskStatusExemptionRepository;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getDailyStatusReportFromDateToDate(Long empId, Timestamp startDate, Timestamp endDate, Pageable pageable) {
		EmployeeStatusReport employee = employeeRepository.findById(empId).orElseThrow(() -> new UserNotFoundException(ConstantValues.USER_NOT_FOUND + ConstantValues.WITH_GIVEN_ID + empId));

		Page<DailyStatusReport> page = this.dailyStatusRepository.findByEmployeeAndStatusReportDateBetween(employee, startDate, endDate, pageable);

		return Loop.forLoop(page);
	}

	@Override
	public void addTask(DailyStatusReportDto dailyStatusReportDto, long empId) throws ReportAlreadyExistsException, NotNullException {
		EmployeeStatusReport employee = employeeRepository.findById(empId).orElseThrow(() -> new UserNotFoundException(ConstantValues.USER_NOT_FOUND + ConstantValues.WITH_GIVEN_ID + empId));



		EmployeeStatusReport assignedBy = employeeRepository.findById(dailyStatusReportDto.getTaskAssignedBy()).orElseThrow(() -> new UserNotFoundException(ConstantValues.USER_NOT_FOUND + ConstantValues.WITH_GIVEN_ID + dailyStatusReportDto.getTaskAssignedBy()));

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = dateFormat.format(timestamp);

		Timestamp newTimestamp = Timestamp.valueOf(formattedDate + " 00:00:00");

		Optional<DailyStatusReport> existingReport = dailyStatusRepository.findByStatusReportDateAndEmployee(newTimestamp, employee);
		if (existingReport.isPresent()) {

			// logger.error(ConstantValues.REPORT + new ReportAlreadyExistsException());

			throw new ReportAlreadyExistsException(ConstantValues.REPORT);

		}
		DailyStatusReport dailyStatusReport = new DailyStatusReport(dailyStatusReportDto);

		if (dailyStatusReport.getStatus().equalsIgnoreCase("no") && dailyStatusReport.getReason() == null) {
			throw new NotNullException(ConstantValues.REASON);
		}

		dailyStatusReport.setEmployee(employee);
		dailyStatusReport.setAssignedBy(assignedBy);
		dailyStatusReport.setStatusReportDate(newTimestamp);
		if (dailyStatusReport.getEmployee() == dailyStatusReport.getAssignedBy()) {
			throw new NotEligibleException(ConstantValues.SHOULD_NOT_SAME);
		}


		//logger.info(ConstantValues.RESULT + dailyStatusReportDto);

		dailyStatusRepository.save(dailyStatusReport);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateVerifiedBy(List<Long> taskDetailsId, long verifiedById) {
		List<DailyStatusReport> dailyStatusReports = this.dailyStatusRepository.findAllById(taskDetailsId);

		if (dailyStatusReports.size() > 0) {
			for (DailyStatusReport reports : dailyStatusReports) {
				EmployeeStatusReport verifiedBy = this.employeeRepository.findById(verifiedById).orElseThrow(() -> new UserNotFoundException(ConstantValues.USER_NOT_FOUND + ConstantValues.WITH_GIVEN_ID + verifiedById));

				reports.setVerifiedBy(verifiedBy);
				this.dailyStatusRepository.save(reports);
			}

		} else {
			logger.error(ConstantValues.NOT_SELECTED);
			throw new DataNotFoundException(ConstantValues.NOT_SELECTED);

		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getAllDailyStatusReports(Pageable pageable) {
		Page<DailyStatusReport> page = this.dailyStatusRepository.findAll(pageable);

		return Loop.forLoop(page);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getAllReportsByEmpId(long empId, Pageable pageable) {
		EmployeeStatusReport employee = employeeRepository.findById(empId).orElseThrow(() -> new UserNotFoundException(ConstantValues.USER_NOT_FOUND + ConstantValues.WITH_GIVEN_ID + empId));
		Page<DailyStatusReport> page = dailyStatusRepository.findByEmployee(employee, pageable);

		return Loop.forLoop(page);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getAllReportsGivenDates(Timestamp fromDate, Timestamp toDate, Pageable pageable) {
		Page<DailyStatusReport> page = this.dailyStatusRepository.findByStatusReportDateBetween(fromDate, toDate, pageable);

		return Loop.forLoop(page);
	}

	@Override
	public Map<String, Object> getPendingReports(Timestamp fromDate, Timestamp toDate) {
		List<DailyStatusReport> list = this.dailyStatusRepository.findByStatusReportDateBetween(fromDate, toDate);

		return Loop.ifLoop(list);
	}

	@Override
	public Map<String, Object> getStatusReport(Timestamp fromDate, Timestamp toDate, String status, Pageable pageable) {
		Page<DailyStatusReport> page = this.dailyStatusRepository.findByStatusReportDateBetweenAndStatus(fromDate, toDate, status, pageable);

		return Loop.forLoop(page);
	}

	@Override
	public Map<String, Object> getAllPendingStatus(String status, Pageable pageable) {

		Page<DailyStatusReport> page = this.dailyStatusRepository.findByStatus(status, pageable);

		return Loop.forLoop(page);
	}

	@Override
	public Map<String, Object> getAllPendingReports() {
		List<DailyStatusReport> list = this.dailyStatusRepository.findAll();

		return Loop.ifLoop(list);
	}

}
