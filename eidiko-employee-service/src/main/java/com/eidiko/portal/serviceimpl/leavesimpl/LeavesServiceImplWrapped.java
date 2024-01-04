package com.eidiko.portal.serviceimpl.leavesimpl;

import com.eidiko.portal.config.employee.SecurityUtil;
import com.eidiko.portal.dto.leaves.EmployeeLeaveStatusDto;
import com.eidiko.portal.dto.leaves.EmployeeLeavesDto;
import com.eidiko.portal.dto.leaves.LeaveStatusDto;
import com.eidiko.portal.dto.leaves.LeaveStatusResponseDto;
import com.eidiko.portal.entities.employee.ResignedEmployee;
import com.eidiko.portal.entities.leaves.EmployeeLeaveAuditStatus;
import com.eidiko.portal.entities.leaves.EmployeeLeaveStatusLeaves;
import com.eidiko.portal.entities.leaves.LeavesAsPerBand;
import com.eidiko.portal.entities.leaves.interfaces.Employee;
import com.eidiko.portal.entities.leaves.interfaces.LeaveStatusCounts;
import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import com.eidiko.portal.exception.taskstatus.UserNotFoundException;
import com.eidiko.portal.repo.employee.ResignedEmpRepo;
import com.eidiko.portal.repo.leaves.EmployeeLeaveStatusRepo;
import com.eidiko.portal.repo.leaves.LeavesAsPerBandRepo;
import com.eidiko.portal.repo.leaves.LeavesAuditRepo;
import com.eidiko.portal.service.leaves.LeavesServiceWrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.security.sasl.AuthenticationException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeavesServiceImplWrapped implements LeavesServiceWrapped {

	@Autowired
	private WebClient client;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private EmployeeLeaveStatusRepo employeeLeaveStatusRepo;

	@Autowired
	private LeavesAuditRepo leavesAuditRepo;

	@Autowired
	private LeavesAsPerBandRepo asPerBandRepo;

	@Autowired
	private ResignedEmpRepo resignedEmpRepo;
	@Override
	@Transactional(rollbackOn = { Exception.class })
	public Map<String, Object> uploadAttendanceExcelSheet(MultipartFile file, String month, String year,
			long modifiedBy) throws Exception {
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("file", file.getResource());
		builder.part("month", month, MediaType.TEXT_PLAIN);
		builder.part("year", year, MediaType.TEXT_PLAIN);
		builder.part("modifiedBy", modifiedBy, MediaType.TEXT_PLAIN);

		String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
		ArrayList<String> list = new ArrayList<>();
		list.add("xls");
		list.add("xlsx");
		list.add("csv");
		if (!list.contains(fileExtension)) {
			throw new Exception("please provide excel file");
		}
		List<String> daysList = new ArrayList<>();

		List<LeaveStatusDto> result = new ArrayList<>();
		try {

			XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
			XSSFSheet sheet = workbook.getSheetAt(0);
			String empId = "";
			String status = "";

			for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
				XSSFRow row = sheet.getRow(i);
				int count = 0;
				for (int j = 2; j < row.getPhysicalNumberOfCells() - 10; j++) {
					DataFormatter df = new DataFormatter();

					if (i == 1) {
						if (j == 2) {
							j = 5;
						}
						String day = df.formatCellValue(row.getCell(j));
						daysList.add(day);
						continue;

					}
					if (j == 2) {
						empId = df.formatCellValue(row.getCell(j));
						j = 5;

					}
					status = df.formatCellValue(row.getCell(j));

					result.add(new LeaveStatusDto(empId, year, month, null, status, count));
					count++;
				}
				if (i == 1) {
					i = 2;

				}

			}
			List<EmployeeLeaveStatusLeaves> employeeLeaveStatusList = addDayToLeavesDto(result, daysList, modifiedBy);
			this.employeeLeaveStatusRepo.saveAll(employeeLeaveStatusList);
			List<Long> employeeLeaveStatusDuplicate = this.employeeLeaveStatusRepo.getEmployeeLeaveStatusDuplicate();
			this.employeeLeaveStatusRepo.deleteAllById(employeeLeaveStatusDuplicate);
			updateAuditDetails("month : year = "+month+":"+year,999999,0,"Added leaves using Excel file :"+file.getOriginalFilename());

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		Map<String, Object> map = new HashMap<>();
		map.put("status", HttpStatus.OK.value());
		map.put("message", "Data updated Scuccessfully");
		map.put("statusMessage", "success");

		return map;

	}

	@Override
	public Map<String, Object> getSpentLeaveCountForEmployees(long empId, int year) throws Exception {
		String fromDate=(year-1)+"-12-26";
		String toDate=year+"-12-25";

		List<EmployeeLeaveStatusLeaves> employeeLeaveStatusList = this.employeeLeaveStatusRepo.findAllByEmpIdInThisYear(fromDate, toDate,empId);
		employeeLeaveStatusList.forEach(t -> {
			t.setModifiedByName(this.employeeLeaveStatusRepo.getEmployeeName(t.getModifiedBy()));
		});
		LeavesAsPerBand leavesAsPerBand = this.asPerBandRepo.findByEmpIdAndYear(empId, year);
		Map<String, Object> map = new HashMap<>();
		map.put("status", HttpStatus.OK.value());
		map.put("result", employeeLeaveStatusList);
		if(leavesAsPerBand!=null){
			map.put("totalLeavesAsPerBand", leavesAsPerBand.getLeaves());
		}
		else{
			map.put("totalLeavesAsPerBand", 0);
		}

		map.put("empName", this.employeeLeaveStatusRepo.getEmployeeName(empId));
		map.put("empId", empId);
		map.put("message", "Data fetched successfully");
		map.put("statusMessage", "success");
		return map;
	}

	@Override
	public Map<String, Object> getEmployeeLeaveStatusReport(int year) throws Exception {
		String startDate = (year - 1) + "-12-26";
		String endDate = year + "-12-25";
		

		List<Employee> allEmployee = this.employeeLeaveStatusRepo.getAllEmployee();
		List<LeaveStatusCounts> empLeaveStatusCount = this.employeeLeaveStatusRepo.getEmpLeaveStatusCount(startDate,
				endDate);
		List<LeaveStatusCounts> empAbsentStatusCount = this.employeeLeaveStatusRepo.getEmpAbsentStatusCount(startDate,
				endDate);

		List<LeavesAsPerBand> leavesAsPerBandList = this.asPerBandRepo.findAllByYear(year);
		List<ResignedEmployee> resgnEmpList = resignedEmpRepo.findAll();
		Set<Long> resignedEmpIds = resgnEmpList.stream()
				.map(ResignedEmployee::getEmpId)
				.collect(Collectors.toSet());

		List<LeaveStatusResponseDto> responseList = new ArrayList<>();

		for (Employee e : allEmployee) {
			LeaveStatusResponseDto responseDto = new LeaveStatusResponseDto();
			responseDto.setEmpId(e.getemp_id());
			responseDto.setEmpName(e.getemp_name());

			empAbsentStatusCount.forEach(ab -> {
				if (e.getemp_id() == ab.getemp_id()) {
					responseDto.setAbsentCount(ab.getcount());
				}
			});

			empLeaveStatusCount.forEach(l -> {
				if (e.getemp_id() == l.getemp_id()) {
					responseDto.setLeavesCount(l.getcount());
				}
			});
			leavesAsPerBandList.forEach(lpb -> {
				if (e.getemp_id() == lpb.getEmpId()) {
					responseDto.setBand(lpb.getBand());
					responseDto.setTotalLeavesAsPerband(lpb.getLeaves());
				}
			});
			responseDto.setTotal(responseDto.getAbsentCount() + responseDto.getLeavesCount());
			if (resignedEmpIds.contains(e.getemp_id())) {
				continue;
			}
			responseList.add(responseDto);
		}

		Map<String, Object> map = new HashMap<>();
		map.put("status", HttpStatus.OK.value());
		map.put("result", responseList);
		map.put("message", "Data fetched successfully");
		map.put("statusMessage", "success");
		return map;
	}
	
	@Override
	public Map<String, Object> getEmployeeLeaveStatusReportMonthly(int year, int month) throws Exception {
		String startDate = "";
		String endDate = "";
		if (month == 0) {
			startDate = (year - 1) + "-12-26";
			endDate = year + "-" + (month + 1) + "-25";
				
		} 
		else if(month == 12) {
			startDate = (year-1) + "-12-26";
			endDate = year + "-" + (month) + "-25";
		}
		else {
			startDate = year + "-" + month + "-26";
			endDate = year + "-" + (month + 1) + "-25";
		}

		List<Employee> allEmployee = this.employeeLeaveStatusRepo.getAllEmployee();
		List<LeaveStatusCounts> empLeaveStatusCount = this.employeeLeaveStatusRepo.getEmpLeaveStatusCount(startDate,
				endDate);
		List<LeaveStatusCounts> empAbsentStatusCount = this.employeeLeaveStatusRepo.getEmpAbsentStatusCount(startDate,
				endDate);

		List<LeavesAsPerBand> leavesAsPerBandList = this.asPerBandRepo.findAllByYear(year);
		List<ResignedEmployee> resgnEmpList = resignedEmpRepo.findAll();
		Set<Long> resignedEmpIds = resgnEmpList.stream()
				.map(ResignedEmployee::getEmpId)
				.collect(Collectors.toSet());

		List<LeaveStatusResponseDto> responseList = new ArrayList<>();

		for (Employee e : allEmployee) {
			LeaveStatusResponseDto responseDto = new LeaveStatusResponseDto();
			responseDto.setEmpId(e.getemp_id());
			responseDto.setEmpName(e.getemp_name());

			empAbsentStatusCount.forEach(ab -> {
				if (e.getemp_id() == ab.getemp_id()) {
					responseDto.setAbsentCount(ab.getcount());
				}
			});

			empLeaveStatusCount.forEach(l -> {
				if (e.getemp_id() == l.getemp_id()) {
					responseDto.setLeavesCount(l.getcount());
				}
			});
			leavesAsPerBandList.forEach(lpb -> {
				if (e.getemp_id() == lpb.getEmpId()) {
					responseDto.setBand(lpb.getBand());
					responseDto.setTotalLeavesAsPerband(lpb.getLeaves());
				}
			});
			responseDto.setTotal(responseDto.getAbsentCount() + responseDto.getLeavesCount());
			if (resignedEmpIds.contains(e.getemp_id())) {
				continue;
			}
			responseList.add(responseDto);
		}

		Map<String, Object> map = new HashMap<>();
		map.put("status", HttpStatus.OK.value());
		map.put("result", responseList);
		map.put("message", "Data fetched successfully");
		map.put("statusMessage", "success");
		return map;
	}

	@Override
	@Transactional(rollbackOn = {Exception.class})
	public Map<String, Object> addLeavesAsPerBand(int year, MultipartFile file, long modifiedBy) throws Exception {
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("file", file.getResource());
		builder.part("modifiedBy", modifiedBy, MediaType.TEXT_PLAIN);

		logger.info("----- Inside Add Leaves per Band Module --------");
		logger.info("year: {}", year);
		logger.info("fileName: {}", file.getOriginalFilename());

		String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
		ArrayList<String> list = new ArrayList<>();
		list.add("xls");
		list.add("xlsx");
		list.add("csv");
		if (!list.contains(fileExtension)) {
			throw new Exception("please provide excel file");
		}

		try {
			
			
			List<LeavesAsPerBand> existingData = this.asPerBandRepo.findAllByYear(year);
			
			List<LeavesAsPerBand> leavesAsPerBandList = new ArrayList<>();

			XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
			XSSFSheet sheet = workbook.getSheetAt(0);
			for (int i = 3; i < sheet.getPhysicalNumberOfRows(); i++) {
				XSSFRow row = sheet.getRow(i);
				DataFormatter df = new DataFormatter();
				String sNo = df.formatCellValue(row.getCell(0));
				String empId = df.formatCellValue(row.getCell(1));
				String band = df.formatCellValue(row.getCell(2));
				String totalLeaves = df.formatCellValue(row.getCell(3));

				if ((!sNo.trim().equals("")) && (!empId.trim().equals("")) && (!band.trim().equals(""))
						&& (!totalLeaves.trim().equals(""))) {
					int empIdInt = Integer.parseInt(empId);

		            // Search for the employee record with the same empId and year
		            LeavesAsPerBand existingRecord = findExistingRecord(existingData, empIdInt, year);

		            if (existingRecord != null) {
		                // Record with empId and year exists in the database, check for changes
		                if (!existingRecord.getBand().equals(band) || 
		                    existingRecord.getLeaves() != Integer.parseInt(totalLeaves)) {
		                    // updating the data
		                    existingRecord.setBand(band);
		                    existingRecord.setLeaves(Integer.parseInt(totalLeaves));
		                    this.asPerBandRepo.save(existingRecord); // Update the existing record
		                }
		            } else {
		                //if it's a new record insert
		                LeavesAsPerBand leavesAsPerBand = new LeavesAsPerBand();
		                leavesAsPerBand.setBand(band);
		                leavesAsPerBand.setEmpId(empIdInt);
		                leavesAsPerBand.setLeaves(Integer.parseInt(totalLeaves));
		                leavesAsPerBand.setYear(year);
		                leavesAsPerBandList.add(leavesAsPerBand); 
		                logger.info("leaves_as_per_employee: {}", leavesAsPerBand);
		            }
		        }
		    }

		    // Insert new records into the database
		    this.asPerBandRepo.saveAll(leavesAsPerBandList);
			
			List<Long> duplicateLeavesAsPerBandIds = this.asPerBandRepo.duplicateLeavesAsPerBandIds();
			if (!duplicateLeavesAsPerBandIds.isEmpty()) {
				this.asPerBandRepo.deleteAllById(duplicateLeavesAsPerBandIds);
			}
			updateAuditDetails("year :"+year,999999,0,"Added Employee leaves as per band with excel file: "+file.getOriginalFilename());

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		Map<String, Object> map = new HashMap<>();
		map.put("status", HttpStatus.OK.value());
		map.put("message", "Data inserted successfully");
		map.put("statusMessage", "success");
		return map;
	}
	
	
	private LeavesAsPerBand findExistingRecord(List<LeavesAsPerBand> existingData, int empId, int year) {
	    for (LeavesAsPerBand record : existingData) {
	        if (record.getEmpId() == empId && record.getYear() == year) {
	            return record;
	        }
	    }
	    return null; // No matching record found
	}

	private List<EmployeeLeaveStatusLeaves> addDayToLeavesDto(List<LeaveStatusDto> list, List<String> days, long modifiedBy) {

		String[] prevMonthDates = { "26", "27", "28", "29", "30", "31" };
		List<String> prevMontDaysList = Arrays.asList(prevMonthDates);
		Calendar calendar = Calendar.getInstance();
		List<EmployeeLeaveStatusLeaves> lists = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			LeaveStatusDto dto = list.get(i);

			if ((dto.getEmpId() != null || !dto.getEmpId().equals("")) && (dto.getStatus().equalsIgnoreCase("L")
					|| dto.getStatus().equalsIgnoreCase("C") || dto.getStatus().equalsIgnoreCase("A"))) {
				String month = dto.getMonth();
				dto.setDay(days.get(dto.getCount()));//25
				calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dto.getDay()));//5.25
				if (prevMontDaysList.contains(dto.getDay())) {
					calendar.set(Calendar.MONTH, (Integer.parseInt(month) - 1));
				} else {
					calendar.set(Calendar.MONTH, Integer.parseInt(month));
				}
				calendar.set(Calendar.YEAR, Integer.parseInt(dto.getYear()));

				EmployeeLeaveStatusLeaves entity = new EmployeeLeaveStatusLeaves();
				entity.setEmpId(Long.parseLong(dto.getEmpId()));
				entity.setEStatus(dto.getStatus().trim().charAt(0));
				entity.setLeaveDate(new Date(calendar.getTimeInMillis()));
				entity.setModifiedBy(modifiedBy);
				lists.add(entity);

			}

		}

		return lists;
	}

	@Override
	@Transactional(rollbackOn = {Exception.class})
	public Map<String,Object> addLeaves(EmployeeLeaveStatusLeaves employeeLeaveStatusLeaves)
	{	
		EmployeeLeaveStatusLeaves existsByempIdAndleaveDate = employeeLeaveStatusRepo.findByEmpIdAndLeaveDate(employeeLeaveStatusLeaves.getEmpId(),employeeLeaveStatusLeaves.getLeaveDate());
		if (existsByempIdAndleaveDate != null) {
			throw new ResourceNotProcessedException("Already EmpId with LeaveDate exists");
		}
		System.out.println(employeeLeaveStatusLeaves);
		employeeLeaveStatusLeaves.getEStatus();
		try {
			employeeLeaveStatusRepo.save(employeeLeaveStatusLeaves);
			updateAuditDetails(new ObjectMapper().writeValueAsString(employeeLeaveStatusLeaves),employeeLeaveStatusLeaves.getEmpId(),
					0,"Added Leaves to the Employee");
		}catch (Exception e){
			e.printStackTrace();
			throw new ResourceNotProcessedException("not processed ...");
		}
		Map<String,Object> map=new HashMap<>();
		map.put("status",HttpStatus.OK.value());
		map.put("message","inserted");
		return  map;
	}

	@Override
	@Transactional(rollbackOn = {Exception.class})
	public Map<String,Object> updateLeaveStatusFields(long leavesStatusId, EmployeeLeaveStatusDto dto) {
		
		EmployeeLeaveStatusLeaves leaveStatus = employeeLeaveStatusRepo.findById(leavesStatusId).orElseThrow(()->new UserNotFoundException("User not found with this Id: "+leavesStatusId));
		Map<String,Object> map=new HashMap<>();
		if(leaveStatus!=null) {
			leaveStatus.setEmpId(dto.getEmpId());
			leaveStatus.setLeaveDate(dto.getLeaveDate());
			String eStatus = dto.geteStatus();
			char firstChar = eStatus.charAt(0);
			leaveStatus.setEStatus(firstChar);
			try {
				employeeLeaveStatusRepo.save(leaveStatus);
				updateAuditDetails(new ObjectMapper().writeValueAsString(dto),dto.getEmpId(),0,"Updated Leave Status with Id :"+ leavesStatusId);
			}catch (Exception e){
				e.printStackTrace();
				throw new ResourceNotProcessedException("not updated ...");
			}
			map.put("status",HttpStatus.OK.value());
			map.put("message","updated");
		}
		return  map;
	}


	@Override
	@Transactional(rollbackOn = {Exception.class})
	public Map<String,Object> deleteByEmpIdAndLeaveDate(long leaveStatusId) {
		Map<String,Object> map=new HashMap<>();
		EmployeeLeaveStatusLeaves byId = employeeLeaveStatusRepo.findById(leaveStatusId).orElseThrow(()->new UserNotFoundException("user not found : "+leaveStatusId));
		try {
			employeeLeaveStatusRepo.delete(byId);
			updateAuditDetails("leave  date :"+byId.getLeaveDate(),byId.getEmpId(),0,"Deleted on Date  : "+ new Timestamp(System.currentTimeMillis()));
		}catch(Exception e){
			e.printStackTrace();
			throw new ResourceNotProcessedException("not deleted ...");
		}
		map.put("status",HttpStatus.OK.value());
		map.put("message","deleted");
		return  map;
	}


	public boolean updateAuditDetails(String payload,long empId,long updatedBy,String description){
		boolean f= false;

		try {
			updatedBy=SecurityUtil.getCurrentUserDetails().getEmpId();
		} catch (AuthenticationException e) {
			throw new RuntimeException(e);
		}

		EmployeeLeaveAuditStatus employeeLeaveAuditStatus = new EmployeeLeaveAuditStatus();

		employeeLeaveAuditStatus.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
		employeeLeaveAuditStatus.setEmpId(empId);
		employeeLeaveAuditStatus.setUpdatedBy(updatedBy);
		employeeLeaveAuditStatus.setDescription(description);
		employeeLeaveAuditStatus.setPayload(payload);
		try {
			this.leavesAuditRepo.save(employeeLeaveAuditStatus);
			f=true;
		}catch (Exception e){
			e.printStackTrace();
			throw new ResourceNotProcessedException("not updated or processed ...");
		}

		return f;
	}


}
