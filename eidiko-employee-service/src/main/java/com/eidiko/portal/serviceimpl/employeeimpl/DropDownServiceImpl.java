package com.eidiko.portal.serviceimpl.employeeimpl;


import com.eidiko.portal.dto.employee.EmpAccessLevelResDto;
import com.eidiko.portal.entities.employee.ClientLocations;
import com.eidiko.portal.entities.employee.Designations;
import com.eidiko.portal.entities.employee.EmployeeAccessLevel;
import com.eidiko.portal.entities.employee.Skills;
import com.eidiko.portal.entities.employee.Teams;
import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.repo.employee.ClientLocationRepo;
import com.eidiko.portal.repo.employee.DesignationRepo;
import com.eidiko.portal.repo.employee.EmpAccessLvlRepo;
import com.eidiko.portal.repo.employee.SkillsRepo;
import com.eidiko.portal.repo.employee.TeamRepo;
import com.eidiko.portal.service.employee.DropDownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class DropDownServiceImpl implements DropDownService {
	
	
	@Autowired
	private ClientLocationRepo clientLocationRepo;
	
	@Autowired
	private SkillsRepo skillsRepo;
	
	@Autowired
	private TeamRepo teamRepo;
	
	@Autowired
	private EmpAccessLvlRepo accessLvlRepo;
	
	@Autowired
	private DesignationRepo designationRepo;

	@Override
	public Map<String, Object> getClientLocations() {
		
		List<ClientLocations> list = this.clientLocationRepo.findAll();
		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
		map.put(ConstantValues.RESULT, list);
		
		return map;
	}

	@Override
	public Map<String, Object> getSkills() {
		
		List<Skills> list = this.skillsRepo.findAll();
		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
		map.put(ConstantValues.RESULT, list);
		
		return map;
	}

	@Override
	public Map<String, Object> getTeams() {

		List<Teams> list = this.teamRepo.findAll();
		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
		map.put(ConstantValues.RESULT, list);
		
		return map;
	}

	@Override
	public Map<String, Object> getAccessLevel() {
		
		List<EmployeeAccessLevel> list = this.accessLvlRepo.findAll();
		List<EmpAccessLevelResDto> res = new ArrayList<>();
		list.forEach(a->{
			res.add(new EmpAccessLevelResDto(a.getAccessLvlId(), a.getAccessLvlName(), a.getAccessLvlDesc()));
		});
		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
		map.put(ConstantValues.RESULT, res);
		
		return map;
	}

	@Override
	public Map<String, Object> getAllDesignations() {
		List<Designations> allDesgnations = this.designationRepo.findAll();
		Map<String, Object> map = new HashMap<>();
		map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
		map.put(ConstantValues.RESULT, allDesgnations);
		
		return map;
	}

}
