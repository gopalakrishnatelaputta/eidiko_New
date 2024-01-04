package com.eidiko.portal.controller.reportingmanager;

import com.eidiko.portal.config.employee.SecurityUtil;
import com.eidiko.portal.dto.employee.CautionDto;
import com.eidiko.portal.entities.reportingmanager.CautionMail;
import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import com.eidiko.portal.helper.employee.AuthAssignedConstants;
import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.helper.employee.Helper;
import com.eidiko.portal.service.reportingmanager.CautionMailService;
import com.eidiko.portal.service.reportingmanager.ReportingManagerServiceWrapped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/warningmail")
public class CautionMailControllerWrapped {

	@Autowired
	private CautionMailService cautionMailService;
	
	@Autowired
	private Helper helper;
	
	private boolean validateUser(long empId) {
		try {
			if (SecurityUtil.getCurrentUserDetails().getEmpId() == empId || this.helper.validateUserOrAuth()) {
				return true;
			}
		} catch (AuthenticationException e) {
			throw new ResourceNotProcessedException("You don't have Authority");
		}
		return false;
	}
	
	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PostMapping("/send-mail/{empId}")
	public ResponseEntity<Map<String, Object>> warningMail(@PathVariable long empId,@ModelAttribute CautionDto cautionMail)
			throws Exception {
		if(cautionMail.getAttachments()!=null &&!cautionMail.getAttachments().isEmpty())
		{
			Map<String, Object> map = this.cautionMailService.cautionMailWithAttchments(empId,cautionMail);
			return ResponseEntity.ok().body(map);
		}
		else
		{
			System.out.println("missing");
			Map<String, Object> map = this.cautionMailService.cautionMail(empId,cautionMail);
			return ResponseEntity.ok().body(map);


		}}
	
	
	
	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@GetMapping("/get-caution-mail-data-by-empId/{empId}")
	public Map<String, Object> getCautionMailDataByEmpId(@PathVariable long empId,
			@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "1000") Integer pageSize,
			@RequestParam(defaultValue = ConstantValues.EMP_ID) String sortBy){
		if (!validateUser(empId)) {
			throw new ResourceNotProcessedException("You don't have Authority");
		}
		Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by(sortBy));
		return cautionMailService.getDescriptionsByEmpId(empId,pageable);
	}
	
	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@GetMapping("/get-caution-mail-data-by-month-year/{year}/{month}")
	public Map<String, Object> getCautionMailDataByMonthAndYear(@PathVariable int year, @PathVariable int month,
			@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "1000") Integer pageSize,
			@RequestParam(defaultValue = ConstantValues.EMP_ID) String sortBy){
		Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by(sortBy));
		return cautionMailService.getDataByMonthAndYear(year,month,pageable);
	}
	
	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@GetMapping("/get-all-caution-mail-data-by-year/{year}")
	public Map<String, Object> getAllCautionMailDataByYear(@PathVariable int year,
			@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "1000") Integer pageSize,
			@RequestParam(defaultValue = ConstantValues.EMP_ID) String sortBy){
		Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by(sortBy));
		return cautionMailService.findBySentDate(year,pageable);
	}
	
	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@GetMapping("/get-caution-mail-report/{empId}/{fromDate}/{toDate}")
	public Map<String, Object> getCautionMailReportByEmpId(@PathVariable LocalDate fromDate,
			@PathVariable LocalDate toDate,@PathVariable long empId){

		return cautionMailService.getCautionReportByDatesAndEmpId(fromDate,toDate,empId);
	}
	
	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@GetMapping("/get-all-caution-mail-report/{fromDate}/{toDate}")
	public Map<String, Object> getAllCautionMailReportBetweenDates(@PathVariable LocalDate fromDate,
			@PathVariable LocalDate toDate){
		return cautionMailService.getCautionReportByDates(fromDate,toDate);
	}
}
