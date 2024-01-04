package com.eidiko.portal.serviceimpl.biometricimpl;

import com.eidiko.portal.dto.biometric.BiometricCalculatedResponseDto;
import com.eidiko.portal.dto.biometric.BiometricReportsDto;
import com.eidiko.portal.entities.biometric.*;
import com.eidiko.portal.entities.employee.ResignedEmployee;
import com.eidiko.portal.exception.biometric.EmployeeNotFoundException;
import com.eidiko.portal.helper.biometric.*;
import com.eidiko.portal.repo.biometric.*;
import com.eidiko.portal.repo.employee.ResignedEmpRepo;
import com.eidiko.portal.service.biometric.BiometricReportService;
import com.eidiko.portal.service.biometric.BiometricServiceGetBioDates;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class  BiometricReportServiceImpl implements BiometricReportService {

	@Autowired
	private BiometricReportRepository biometricReportRepository;

	@Autowired
	private BiometricServiceGetBioDates biometricServiceGetBioDates;

	@Autowired
	private EmployeeRepoBiometric employeeRepo;

	@Autowired
	private BiometricExemptionEmpRepo biometricExemptionEmpRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	EmpHolidayRepo empHolidayRepo;

	@Autowired
	EmpWorkLocationRepo empWorkLocationRepo;

	@Autowired
	EmpLeavesRepo empLeavesRepo;

	@Autowired
	EmpBioMissingReportRepo bioMissingReportRepo;
	
	@Autowired
	private ResignedEmpRepo resignedEmpRepo;

	@Override
	public Map<String, Object> getBioReportsFromDatetoTodateforEmp(long empId, Date fromDate, Date toDate,
			Pageable pageable) {
		Map<String, Object> map = new HashMap<>();
		List<BiometricReportEntity> empIdData = biometricReportRepository.findByempId(empId);
		if (empIdData.isEmpty()) {
			log.info("Not Exist");
			log.info(empId + "empID");
			throw new EmployeeNotFoundException("Employee Not Found ");

		}

		Page<BiometricReportEntity> page = biometricReportRepository.findByEmpIdAndBiometricDateBetween(empId, fromDate,
				toDate, pageable);

		if (page.hasContent()) {
			map.put(ConstantValues.RESULT, page.getContent());
			map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put("totalPages", page.getTotalPages());
			map.put("noOfElements", page.getNumberOfElements());
			map.put("pageSize", page.getSize());
			map.put("totalElements", page.getTotalElements());
			map.put("isFirst", page.isFirst());
			map.put("isLast", page.isLast());

			return map;
		}

		else {

			map.put(ConstantValues.MESSAGE, ConstantValues.NO_DATA_FETCHED_SUCCESS_TEXT);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.RESULT, new ArrayList<>());
			return map;
		}

	}

	public boolean isEmpIdNotExists(int empId) {
		List<BiometricReportEntity> biometricReportEntities = biometricReportRepository.getByempId(empId);
		return biometricReportEntities == null;
	}

	@Override
	public Map<String, Object> getAllBiometricReportsByDate(Date date, Pageable pageable) {
		Map<String, Object> map = new HashMap<>();

		Page<BiometricReportEntity> pageable2 = this.biometricReportRepository.findByBiometricDate(date, pageable);
		if (pageable2.hasContent()) {
			map.put(ConstantValues.RESULT, pageable2.getContent());
			map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put("totalPages", pageable2.getTotalPages());
			map.put("noOfElements", pageable2.getNumberOfElements());
			map.put("pageSize", pageable2.getSize());
			map.put("totalElements", pageable2.getTotalElements());
			map.put("isFirst", pageable2.isFirst());
			map.put("isLast", pageable2.isLast());
			return map;
		}
		map.put(ConstantValues.MESSAGE, ConstantValues.NO_DATA_FETCHED_SUCCESS_TEXT);
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.RESULT, new ArrayList<>());

		return map;
	}

	@Override
	public Map<String, Object> getCountofisLateBiometricReportByMonth(String month, Integer year, Pageable pageable) {

		if (year == null) {
			year = Year.now().getValue();
		}
		log.info("YEARYAERYAER" + "  " + year);
		Page<BiometricReportEntity> reports = biometricReportRepository.findByMonthAndYear(month, year, pageable);
		Map<String, Object> responseMap = new HashMap<>();
		Map<Long, Integer> counts = new HashMap<>();

		List<ResignedEmployee> resgnEmpList = resignedEmpRepo.findAll();
		Set<Long> resignedEmpIds = resgnEmpList.stream()
				.map(ResignedEmployee::getEmpId)
				.collect(Collectors.toSet());

		for (BiometricReportEntity report : reports) {
			if (report.getIsLate() == true) {
				Long empId = report.getEmpId();
				counts.put(empId, counts.getOrDefault(empId, 0) + 1);
			}
		}
		List<Map<String, Object>> result = new ArrayList<>();
		for (Long empId : counts.keySet()) {
			Map<String, Object> row = new HashMap<>();
			row.put("empId", empId);
			row.put("countOfIsLate", counts.get(empId));
			if (resignedEmpIds.contains(empId)) {
				continue; // Skip processing resigned employees
			}
			result.add(row);
		}
		Collections.sort(result, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				Long empId1 = (Long) o1.get("empId");
				Long empId2 = (Long) o2.get("empId");
				return empId1.compareTo(empId2);
			}
		});
		if (reports.hasContent()) {
			responseMap.put(ConstantValues.RESULT, result);
			responseMap.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
			responseMap.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
			responseMap.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			responseMap.put("totalPages", reports.getTotalPages());
			responseMap.put("noOfElements", reports.getNumberOfElements());
			responseMap.put("pageSize", reports.getSize());
			responseMap.put("totalElements", reports.getTotalElements());
			responseMap.put("isFirst", reports.isFirst());
			responseMap.put("isLast", reports.isLast());
			return responseMap;
		}

		responseMap.put(ConstantValues.MESSAGE, ConstantValues.NO_DATA_FETCHED_SUCCESS_TEXT);
		responseMap.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		responseMap.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		responseMap.put(ConstantValues.RESULT, new ArrayList<>());

		return responseMap;
	}

	@Override
	public Map<String, Object> updateBiometricIsLateReport(Timestamp fromDate, Timestamp toDate) throws SQLException {
		log.info(">>>>>>>>>> Inside report service Implementation");
        log.info("fromDate :"+fromDate+" "+ "toDate :"+toDate);
		// 1. Get All Employees list 
		List<EmployeeBiometric> employeeList = this.employeeRepo.findAllByIsDeleted(true);
		
		int year = fromDate.getYear();

		// 2. Initialize total Biometric Report list
		List<BiometricReportsDto> totalList = new ArrayList<>();
		for (EmployeeBiometric employee : employeeList) {
			Map<String, Object> biometricDataByEmpId = this.biometricServiceGetBioDates
					.getBiometricDataByEmpId(fromDate, toDate, employee.getEmpId());
			
			List<BiometricEntity> biometricData = (List<BiometricEntity>) biometricDataByEmpId
					.get(ConstantValues.RESULT);
			Set<EmpShiftTimingsBiometric> empShiftTimings = employee.getEmpShiftTimings();
			List<BiometricReportsDto> isLateReport = calculateBiometricIsLateReport(employee.getEmpId(), biometricData,
					empShiftTimings, fromDate, toDate);

			totalList.addAll(isLateReport);

		}

		List<BiometricReportEntity> biometricReportEntities = new ArrayList<>();
		totalList.forEach(b -> {
			b.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			biometricReportEntities.add(this.modelMapper.map(b, BiometricReportEntity.class));
		});
		try {
			this.biometricReportRepository.saveAll(biometricReportEntities);
			deleteDuplicateRecord();
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}

		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.MESSAGE, "Data Successfully Updated...");
		map.put("statusMessage", ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		return map;
	}

	private List<BiometricReportsDto> calculateBiometricIsLateReport(long empId, List<BiometricEntity> biometricData,
			Set<EmpShiftTimingsBiometric> shiftTimingsSet, Timestamp fromDate, Timestamp toDate) {

		List<BiometricReportsDto> reportList = new ArrayList<>();

		Calendar today = Calendar.getInstance();
		today.setTimeInMillis(toDate.getTime());
		Calendar calculateDate = Calendar.getInstance();
        calculateDate.setTimeInMillis(fromDate.getTime());
		for (; calculateDate.before(today); calculateDate.add(Calendar.DATE, 1)) {
			Time startTime = getShiftStartTime(calculateDate.getTime(), shiftTimingsSet);
			Calendar biometricStartTime = Calendar.getInstance();
			biometricStartTime.setTimeInMillis(calculateDate.getTimeInMillis());
			biometricStartTime.set(Calendar.HOUR_OF_DAY, startTime.getHours() - 3);
			biometricStartTime.set(Calendar.MINUTE, startTime.getMinutes());
			Calendar biometricEndDate = Calendar.getInstance();
			biometricEndDate.setTimeInMillis(biometricStartTime.getTimeInMillis());
			biometricEndDate.add(Calendar.HOUR_OF_DAY, 24);

			List<java.util.Date> biometricDateList = filterBiometricData(biometricData, biometricStartTime,
					biometricEndDate);

			if (!biometricDateList.isEmpty()) {
				BiometricReportsDto reportsDto = new BiometricReportsDto();
				reportsDto.setBiometricDate(calculateDate.getTime());
				reportsDto.setEmpId(empId);
				reportsDto.setMonth(calculateDate.get(Calendar.MONTH));
				reportsDto.setYear(calculateDate.get(Calendar.YEAR));
				long checkInTime = biometricDateList.get(0).getTime();
				long checkOutTime = biometricDateList.get(biometricDateList.size() - 1).getTime();
				reportsDto.setCheckInTime(new Timestamp(checkInTime));
				reportsDto.setCheckOutTime(new Timestamp(checkOutTime));
				Calendar checkinCal = Calendar.getInstance();
				checkinCal.setTimeInMillis(checkInTime);
				Calendar shiftTime = Calendar.getInstance();
				shiftTime.setTimeInMillis(biometricStartTime.getTimeInMillis());
				shiftTime.add(Calendar.HOUR_OF_DAY, 3);
				if (checkinCal.after(shiftTime)) {
					reportsDto.setIsLate(true);
				} else {
					reportsDto.setIsLate(false);
				}
				shiftTime.add(Calendar.MINUTE, 30);
				if (checkinCal.after(shiftTime)) {
					reportsDto.setVerylate(true);
				} else {
					reportsDto.setVerylate(false);
				}
				int minutes = (int) ((checkOutTime - checkInTime) / (60 * 1000));
				reportsDto.setTotalWorkingTime(minutes);

				reportList.add(reportsDto);
			}

		}
		return reportList;

	}

	private List<java.util.Date> filterBiometricData(List<BiometricEntity> biometricDataList,
			Calendar biometricStartTime, Calendar biometricEndDate) {
		List<java.util.Date> filteredData = new ArrayList<>();
		for (BiometricEntity biometricData : biometricDataList) {
			Timestamp bioDate = biometricData.getBioDate();
			SimpleDateFormat op = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			Calendar biometricDate = formatDate(op.format(bioDate));
			if (biometricStartTime.after(biometricDate)) {
				continue;
			}
			if (biometricEndDate.before(biometricDate)) {
				break;
			}
			filteredData.add(biometricDate.getTime());
		}

		return filteredData;
	}

	private Calendar formatDate(String bioDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		Calendar cal = Calendar.getInstance();
		try {
			java.util.Date d = sdf.parse(bioDate);
			cal.setTime(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return cal;
	}

	private Time getShiftStartTime(java.util.Date calulatedDate, Set<EmpShiftTimingsBiometric> shiftTimingsSet) {

		for (EmpShiftTimingsBiometric empShiftTimings : shiftTimingsSet) {
			java.util.Date startDate = empShiftTimings.getStartDate();
			java.util.Date endDate = empShiftTimings.getEndDate();
			if (endDate == null) {
				endDate = Calendar.getInstance().getTime();
			}
			if ((DateUtils.isSameDay(calulatedDate, startDate) || calulatedDate.after(startDate))
					&& (DateUtils.isSameDay(calulatedDate, endDate) || calulatedDate.before(endDate))) {
				return empShiftTimings.getShiftStartTime();
			}
		}

		return new Time(10, 0, 0);
	}

	private boolean deleteDuplicateRecord() throws SQLException {
		List<DuplicateIdsProjection> biometricReportIds = this.biometricReportRepository
				.findDuplicatesInBiometricReport();
		List<Long> reportIds = new ArrayList<>();
		for (DuplicateIdsProjection l : biometricReportIds) {
			reportIds.add(l.getBIOMETRIC_REPORT_ID());
		}
		try {
			this.biometricReportRepository.deleteAllById(reportIds);
		} catch (Exception e) {
			throw new SQLException("Resource not processed ...");
		}
		return false;

	}

	@Override
	public Map<String, Object> calculatedBiometricEmployeesReport(Pageable pageable, String fromDate, String toDate) {
		log.info("Inside getAllBiometricReportsOfNonLateEmps method");

		Page<BiometricReportProjection> nonLateEmps = biometricReportRepository.findByisLatefalse(fromDate, toDate,
				pageable);

		Page<BiometricReportProjection> late = biometricReportRepository.findByisLatetrue(fromDate, toDate, pageable);

		Page<BiometricReportProjection> greaterThan9Hrs = biometricReportRepository.findBytotalworkingtime(fromDate,
				toDate, 540, pageable);

		Page<BiometricReportProjection> lessThan9hrs = biometricReportRepository
				.findBytotalworkingtimeLess9hrs(fromDate, toDate, 540, pageable);

		Page<BiometricReportProjection> veryLatetrue = biometricReportRepository.findByisVeryLatetrue(fromDate, toDate,
				pageable);

		List<EmployeeBiometric> employees = this.employeeRepo.findAllByIsDeleted(true);

		Map<String, Object> map = new HashMap<>();

		List<BiometricCalculatedResponseDto> biometricCalculatedResponseDtos = new ArrayList<>();
		List<BiometricExemptionEmp> empBiosExemptionsList = this.biometricExemptionEmpRepo.findAll();
		
		List<ResignedEmployee> resgnEmpList = resignedEmpRepo.findAll();
		Set<Long> resignedEmpIds = resgnEmpList.stream()
				.map(ResignedEmployee::getEmpId)
				.collect(Collectors.toSet());

		for (EmployeeBiometric e : employees) {

			if (validateEmpBioExemption(empBiosExemptionsList, e.getEmpId())) {
				continue;
			}

			BiometricCalculatedResponseDto responseDto = new BiometricCalculatedResponseDto();
			responseDto.setEmpId(e.getEmpId());
			responseDto.setEmpName(e.getEmpName());

			Page<BiometricReportEntity> bioReportData = biometricReportRepository.findByEmpIdAndBiometricDateBetween(
					e.getEmpId(), getDateFromatFromString(fromDate), getDateFromatFromString(toDate), pageable);
			long workTime = 0;
			long count = bioReportData.getContent().size();
			for (BiometricReportEntity b : bioReportData) {
				workTime = workTime + Long.parseLong(b.getTotalWorkingTime());
			}
			log.info(workTime + "-----------------" + bioReportData.getContent().size());
			if (workTime > 0 && count > 0) {
				responseDto.setAvgworkingMinutes(workTime / bioReportData.getContent().size());
			}
			for (BiometricReportProjection b : nonLateEmps) {
				if (b.getemp_id() == e.getEmpId()) {
					responseDto.setNonLateCount(b.getcount());
				}
			}
			for (BiometricReportProjection b : late) {
				if (b.getemp_id() == e.getEmpId()) {
					responseDto.setLateCount(b.getcount());
				}
			}
			for (BiometricReportProjection b : greaterThan9Hrs) {
				if (b.getemp_id() == e.getEmpId()) {
					responseDto.setGreaterThanWrkHrsCount(b.getcount());
				}
			}
			for (BiometricReportProjection b : lessThan9hrs) {
				if (b.getemp_id() == e.getEmpId()) {
					responseDto.setLessThanWrkHrsCount(b.getcount());
				}
			}
			for (BiometricReportProjection b : veryLatetrue) {
				if (b.getemp_id() == e.getEmpId()) {
					responseDto.setVeryLateCount(b.getcount());
				}
			}
			if (count > 0) {
				if (resignedEmpIds.contains(e.getEmpId())) {
					continue;
				}
				biometricCalculatedResponseDtos.add(responseDto);
			}

		}

		map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.RESULT, biometricCalculatedResponseDtos);
		return map;

	}

	public Date getDateFromatFromString(String date) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date parsedDate = dateFormat.parse(date);
			return new Date(parsedDate.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private boolean validateEmpBioExemption(List<BiometricExemptionEmp> biometricExemptionEmps, long empId) {
		for (BiometricExemptionEmp be : biometricExemptionEmps) {
			if (be.getEmpId() == empId) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Map<String, Object> biometricReportView(long empId, Integer year) {
		List<BiometricReportViewProjection> listOfBiometricReportsView = biometricReportRepository
				.findByEmpIdAndYear(empId, year);

		List<String> monthList=Arrays.asList(
				"0","1","2","3","4","5","6","7","8","9","10","11");

		List<BiometricReportViewProjection> projectionList = monthList.stream()
				.map(monthDto ->
						listOfBiometricReportsView.stream()
								.filter(report -> report.getmonth().equalsIgnoreCase(monthDto))
								.findFirst()
								.orElse(null)
				)
				.collect(Collectors.toList());

		Map<String, Object> map = new HashMap<>();
		if (!projectionList.isEmpty()) {
			map.put(ConstantValues.RESULT, projectionList);
			map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			return map;
		}
		map.put(ConstantValues.MESSAGE, ConstantValues.NO_DATA_FETCHED_SUCCESS_TEXT);
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.RESULT, new ArrayList<>());

		return map;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public Map<String, Object> updateEmployeesBiometricMissingReport(LocalDate fromDate, LocalDate toDate,
			long modifiedBy) throws SQLException {
		log.info("fromDate"+fromDate + "----------" +"toDate "+ toDate);
		LocalDate highestBiometricDate = this.biometricReportRepository.getHighestBiometricDate(fromDate.getMonth().getValue());
		if (highestBiometricDate == null){
			throw new DateTimeException("please update biometric report for the month: "+ toDate.getMonth().toString());
		}
		if(highestBiometricDate.getDayOfMonth()==25){
			toDate=highestBiometricDate;
		}else {
			toDate = highestBiometricDate;
		}
		
		
		log.info("fromDate-----"+fromDate + "----------" +"toDate-----"+ toDate);
		List<EmpBiometricMissingReport> missingReports = bioMissingReportRepo.findAllByMissingDateBetween(fromDate,toDate);
		// Get All Employee
		List<EmployeeBiometric> employees = this.employeeRepo.findAllByIsDeleted(true);
		List<BiometricExemptionEmp> exemptionEmpList = this.biometricExemptionEmpRepo.findAll();
		
		
		
		Map<String, Object> map = new HashMap<>();
		List<EmpBiometricMissingReport> list = new ArrayList<>();
		for (EmployeeBiometric e : employees) {
//			if (e.getEmpId() != 1042)
//				continue;
			log.info("employee : " + e.getEmpId() + ":" + e.getEmpName());
			Date dateOfJoining = e.getDateOfJoining();
			LocalDate doj= dateOfJoining.toLocalDate();
			if(fromDate.isBefore(doj)) {
				fromDate = doj;
				
			}
			log.info(e.getEmpId() + "---------------------------" + fromDate);
			
			
			// 1. validate from exemption table
			if (validateEmpBioExemption(exemptionEmpList, e.getEmpId())) {
				continue;
			}
			for (LocalDate date = fromDate; date.isBefore(toDate.plusDays(1)); date = date.plusDays(1)) {
				log.info("{}",date);
				// 2. exemption validation
				if (!this.biometricReportRepository.existsByEmpIdAndBiometricDate(e.getEmpId(), date)) {

					// 3. work location validation
					if (workLocationValid(e.getEmpId(), date)) {
						// 4. Holiday List Validation
						// List<HolidayList> holidayListOnDay =
						// this.empHolidayRepo.findAllByHolidayDate(date);
						if (!this.empHolidayRepo.existsByHolidayDate(date)) {
							// 5. Leave Validation
							if (!this.empLeavesRepo.existsByEmpIdAndLeaveDate(e.getEmpId(), date)) {
								// 6. Weekend Validation
								if (!weekendValidation(date, e.getEmpShiftTimings())) {
									EmpBiometricMissingReport mapres = new EmpBiometricMissingReport();
									mapres.setEmpId(e.getEmpId());
									mapres.setMissingDate(Date.valueOf(date));
									mapres.setMissingMonth(date.getMonthValue());
									mapres.setMissingYear(date.getYear());
									mapres.setModifiedBy(modifiedBy);
									list.add(mapres);
								} else {
									log.info(date + ": is a weekend");
								}

							} else {
								log.info(date + ": is a holiday");
							}

						} else {
							log.info(date + ": is holiday");
						}

					} else {
						log.info(e.getEmpId() + " is not working from office on " + date);
					}

				} else {
					log.info(e.getEmpId() + " is present in report table on date " + date);
				}

			}
		}

		try {
			this.bioMissingReportRepo.deleteAll(missingReports);
		    this.bioMissingReportRepo.saveAll(list);
			//List<EmpBiometricMissingReport> report = this.bioMissingReportRepo.getDuplicatesDataFromMissingReport();
			//this.bioMissingReportRepo.deleteAll(report);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
		map.put(ConstantValues.MESSAGE, "Missing report Successfully Updated...");
		map.put("statusMessage", ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		return map;
		
	}
		

	private boolean workLocationValid(long empId, LocalDate date) {

		List<EmployeeWorkingLocationBiometric> wl = this.empWorkLocationRepo.findAllByEmpId(empId);
		Date d = Date.valueOf(date);
		for (EmployeeWorkingLocationBiometric employeeWorkingLocation : wl) {

			if (employeeWorkingLocation.getWorkingFrom().equals("WFO")
					&& (DateUtils.isSameDay(d, employeeWorkingLocation.getStartDate())
							|| employeeWorkingLocation.getStartDate().before(d))
					&& (employeeWorkingLocation.getEndDate() == null
							|| DateUtils.isSameDay(d, employeeWorkingLocation.getEndDate())
							|| employeeWorkingLocation.getEndDate().after(d))) {
				log.info(empId + ": is working from " + employeeWorkingLocation.getWorkingFrom() + " on "
						+ date + ">>>" + employeeWorkingLocation.getEmpWorkLocationId());
				return true;
			}

		}
		return false;
	}

	private boolean weekendValidation(LocalDate date, Set<EmpShiftTimingsBiometric> empShiftTimings) {
		Date d = Date.valueOf(date);
		log.info(String.valueOf(date.getDayOfWeek().getValue()) + "   : " + d);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		for (EmpShiftTimingsBiometric e : empShiftTimings) {

			if ((DateUtils.isSameDay(d, e.getStartDate()) || e.getStartDate().before(d))
					&& (e.getEndDate() == null || DateUtils.isSameDay(d, e.getEndDate()) || e.getEndDate().after(d))) {
				log.info(e.getEmployee().getEmpId() + ": is having weekends at "
						+ String.join(",", e.getWeekOff()) + " on :" + date);

				if (e.getWeekOff().contains(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)))) {
					return true;
				}

			}
		}

		return false;
	}

	@Override
	public Map<String, Object> getMissingReportforAllEmployeesYearly(int year) throws SQLException {
		log.info("---------- Inside Get Missing Report Yearly -------------- : " + year);

		List<MissingReportProjection> missingReportYearly = this.bioMissingReportRepo.getMissingReportYearly(year);
		List<MissingReportProjection> missingReportYearlyFiltered = new ArrayList<>();

		List<ResignedEmployee> resgnEmpList = resignedEmpRepo.findAll();
		Set<Long> resignedEmpIds = resgnEmpList.stream()
				.map(ResignedEmployee::getEmpId)
				.collect(Collectors.toSet());

		missingReportYearly.forEach(m->{
			if(this.employeeRepo.findById(m.getemp_id()).get().isDeleted() == true) {

				if (resignedEmpIds.contains(m.getemp_id())) {
					return;
				}

				missingReportYearlyFiltered.add(m);
			}
		});
		Map<String, Object> map = new HashMap<>();

		map.put(ConstantValues.MESSAGE, "data fetched successfully");
		map.put("statusMessage", ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.RESULT, missingReportYearlyFiltered);

		return map;
	}

	@Override
	public Map<String, Object> getMissingReportforAllEmployeesMonthly(int year, int month) throws SQLException {
		List<MissingReportProjection> missingReportMonthly = this.bioMissingReportRepo.getMissingReportmonthly(year,
				month);
		List<MissingReportProjection> missingReportMonthlyFiltered = new ArrayList<>();

		List<ResignedEmployee> resgnEmpList = resignedEmpRepo.findAll();
		Set<Long> resignedEmpIds = resgnEmpList.stream()
				.map(ResignedEmployee::getEmpId)
				.collect(Collectors.toSet());

		missingReportMonthly.forEach(m->{
			if(this.employeeRepo.findById(m.getemp_id()).get().isDeleted() == true) {
				if (resignedEmpIds.contains(m.getemp_id())) {
					return;
				}
				missingReportMonthlyFiltered.add(m);
			}
		});
		
		Map<String, Object> map = new HashMap<>();

		map.put(ConstantValues.MESSAGE, "data fetched successfully");
		map.put("statusMessage", ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.RESULT, missingReportMonthlyFiltered);

		return map;
	}

	@Override
	public Map<String, Object> getMissingReportforEmployeeYearly(int year, long empId) throws SQLException {

		List<EmpBiometricMissingReport> list = this.bioMissingReportRepo.findAllByEmpIdAndMissingYear(empId, year);
		Map<String, Object> map = new HashMap<>();
		list.forEach(l->l.setEmpName(this.employeeRepo.findById(l.getEmpId()).get().getEmpName()));
		map.put(ConstantValues.MESSAGE, "Data fetched successfully");
		map.put("statusMessage", ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.RESULT, list);

		return map;

	}


	public static void main(String[] args) {

		var date = LocalDate.now();

		java.util.Date date1 = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

		Calendar calculateDate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
		calculateDate.setTimeInMillis(date1.getTime());

		int year = 2024;
		int month = 1;

		YearMonth yearMonth = YearMonth.of(year, month + 1); // Month in YearMonth is 1-based
		LocalDate localStartDate = yearMonth.atDay(1);
		LocalDate localEndDate = yearMonth.atEndOfMonth();

		System.out.println("Local Start Date: " + localStartDate);
		System.out.println("Local End Date: " + localEndDate);

//		System.out.println(today);
//		System.out.println(calculateDate);



	}

}
