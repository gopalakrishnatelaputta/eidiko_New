package com.eidiko.portal.controller.taskstatus;

import com.eidiko.portal.config.employee.SecurityUtil;
import com.eidiko.portal.entities.taskstatus.EmpSkillsTracking;
import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import com.eidiko.portal.helper.employee.AuthAssignedConstants;

import com.eidiko.portal.helper.employee.Helper;
import com.eidiko.portal.helper.taskstatus.ConstantValues;
import com.eidiko.portal.service.taskstatus.EmpSkillsTrackingServiceWrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/emp-skills-tracking")
public class EmpSkillsTrackingControllerWrapped {
	
	@Autowired
	private Helper helper;
	
	@Autowired
	private EmpSkillsTrackingServiceWrapped empSkillsTrackingService;
	Map<String, Object> map = new HashMap<>();
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
	
	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@PostMapping("/insert-skill-data")
	public Map<String, Object> insertSkillData(@RequestBody EmpSkillsTracking empSkillsTracking)
			throws AuthenticationException {
		if (!validateUser(empSkillsTracking.getEmpId())) {
			throw new ResourceNotProcessedException("You don't have Authority");
		}
		

		this.empSkillsTrackingService.insert(empSkillsTracking);
		map.put(ConstantValues.MESSAGE, ConstantValues.CREATED);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.CREATED.value());
		map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
		return map;

	}
	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@GetMapping("/get-skills-by-empid/{empId}")
	public Map<String, Object> getSkillByEmpId(@PathVariable long empId)
			throws URISyntaxException, JsonMappingException, JsonProcessingException {
		if (!validateUser(empId)) {
			throw new ResourceNotProcessedException("You don't have Authority");
		}

		return this.empSkillsTrackingService.getSkillByEmpId(empId);
	}
	
	@PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
	@GetMapping("/get-by-skill-and-working")
	public Map<String, Object> getSkillByEmpId(@RequestParam String skill,@RequestParam String working) {

		return this.empSkillsTrackingService.getBySkillAndWorking(skill,working);
	}
	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@PutMapping("/update-by-skillid/{skillId}")
	public Map<String, Object> updateBySkillId(@RequestBody EmpSkillsTracking empSkillsTracking,@PathVariable long skillId) {
		if (!validateUser(empSkillsTracking.getEmpId())) {
			throw new ResourceNotProcessedException("You don't have Authority");
		}

		this.empSkillsTrackingService.updateBySkillId(empSkillsTracking,skillId);
		map.put(ConstantValues.MESSAGE, ConstantValues.UPDATED);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.CREATED.value());
		map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);

		return map;
	}

	@GetMapping("/get-all-present-skills")
	public ResponseEntity<Map<String, Object>> getAllPresentSkills() {

		return  empSkillsTrackingService.getAllPresentSkills();
	}

	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@DeleteMapping("/delete-skill/{skillId}")
	public  Map<String,Object> deleteSkill(@PathVariable long skillId)
	{
		return this.empSkillsTrackingService.deleteSkill(skillId);
	}
}
