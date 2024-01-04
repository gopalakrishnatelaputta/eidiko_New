package com.eidiko.portal.serviceimpl.taskstatusimpl;

import com.eidiko.portal.config.employee.SecurityUtil;
import com.eidiko.portal.entities.employee.Employee;
import com.eidiko.portal.entities.employee.ResignedEmployee;
import com.eidiko.portal.entities.taskstatus.EmpSkillsTracking;
import com.eidiko.portal.entities.taskstatus.EmployeeStatusReport;
import com.eidiko.portal.exception.employee.ResourceNotFoundException;
import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import com.eidiko.portal.exception.taskstatus.UserNotFoundException;
import com.eidiko.portal.helper.taskstatus.ConstantValues;
import com.eidiko.portal.repo.employee.ResignedEmpRepo;
import com.eidiko.portal.repo.taskstatus.EmpSkillsTrackingRepository;
import com.eidiko.portal.repo.taskstatus.EmployeeRepository;
import com.eidiko.portal.service.taskstatus.EmpSkillsTrackingServiceWrapped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmpSkillsTrackingServiceImplWrapped implements EmpSkillsTrackingServiceWrapped {

	@Autowired
	private EmpSkillsTrackingRepository skillsTrackingRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private ResignedEmpRepo resignedEmpRepo;
	
	Map<String, Object> map = new HashMap<>();
	@Override
	public void insert(EmpSkillsTracking empSkillsTracking) throws AuthenticationException {
		Employee userDetails = SecurityUtil.getCurrentUserDetails();
		empSkillsTracking.setModifiedBy(userDetails.getEmpId());

		empSkillsTracking.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		this.skillsTrackingRepository.save(empSkillsTracking);

	}

	@Override
	public Map<String, Object> getSkillByEmpId(long empId) {
		EmployeeStatusReport employee1=this.employeeRepository.findById(empId).orElseThrow(() -> new ResourceNotFoundException());;
		List<EmpSkillsTracking> empSkillsTracking = this.skillsTrackingRepository.findAllByEmpId(empId);
		List<EmpSkillsTracking> empSkillsTrackings = new ArrayList<>();
		for (EmpSkillsTracking skill : empSkillsTracking) {
			long modifiedBy = skill.getModifiedBy();
			long empId1 = skill.getEmpId();
			EmployeeStatusReport employee = this.employeeRepository.findById(modifiedBy).orElseThrow(() -> new ResourceNotFoundException());

			skill.setModifiedByName(employee.getEmpName());
			skill.setEmpName(employee1.getEmpName());
			skill.setDateOfJoining(employee1.getDateOfJoining());
			empSkillsTrackings.add(skill);
		}

		if (!empSkillsTracking.isEmpty()) {

			map.put(ConstantValues.RESULT, empSkillsTrackings);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
			map.put("dateOfJoining",employee1.getDateOfJoining());

		} else {
			map.put(ConstantValues.RESULT, empSkillsTrackings);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.FAILED_MESSAGE);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
			map.put("dateOfJoining",employee1.getDateOfJoining());
		}

		return map;
	}

	@Override
	public Map<String, Object> getBySkillAndWorking(String skill, String working) {
		List<EmpSkillsTracking> empSkillsTrackings = new ArrayList<>();
		List<ResignedEmployee> resgnEmpList = resignedEmpRepo.findAll();
		Set<Long> resignedEmpIds = resgnEmpList.stream()
		        .map(ResignedEmployee::getEmpId)
		        .collect(Collectors.toSet());
		if(skill.equalsIgnoreCase("All")) {

			if(working.equalsIgnoreCase("ALL")) {
				List<EmpSkillsTracking> findAll = this.skillsTrackingRepository.findAll();

				System.out.println("resgnEmpList-------------------"+resgnEmpList);
				for (EmpSkillsTracking skills : findAll) {
					long modifiedBy = skills.getModifiedBy();
					long empId1 = skills.getEmpId();
					System.out.println("empID1------"+empId1);
					if (resignedEmpIds.contains(empId1)) {
						
						System.out.println(resignedEmpIds.contains("empId1##########" +empId1));
				        continue; // Skip processing resigned employees
				    }
					
					EmployeeStatusReport employee = this.employeeRepository.findById(modifiedBy).orElseThrow(() -> new UserNotFoundException());
					EmployeeStatusReport employee1 = this.employeeRepository.findById(empId1).orElseThrow();
					skills.setModifiedByName(employee.getEmpName());
					skills.setEmpName(employee1.getEmpName());
					skills.setDateOfJoining(employee1.getDateOfJoining());
					empSkillsTrackings.add(skills);
				}
				map.put(ConstantValues.RESULT, empSkillsTrackings);
				map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
				map.put(ConstantValues.MESSAGE, ConstantValues.SUCCESS_MESSAGE);
				map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
				return map;
			}
			List<EmpSkillsTracking> allByWorking = this.skillsTrackingRepository.findAllByWorking(working);
			for (EmpSkillsTracking skills : allByWorking) {
				long modifiedBy = skills.getModifiedBy();
				long empId1 = skills.getEmpId();
				if (resignedEmpIds.contains(empId1)) {
			        continue; // Skip processing resigned employees
			    }
				EmployeeStatusReport employee = this.employeeRepository.findById(modifiedBy).orElseThrow(() -> new ResourceNotFoundException());
				EmployeeStatusReport employee1 = this.employeeRepository.findById(empId1).orElseThrow();
				skills.setModifiedByName(employee.getEmpName());
				skills.setEmpName(employee1.getEmpName());
				skills.setDateOfJoining(employee1.getDateOfJoining());
				empSkillsTrackings.add(skills);
			}
		}else {
			List<EmpSkillsTracking> empSkillsTracking = this.skillsTrackingRepository.findBySkillsAndWorking(skill, working);

			for (EmpSkillsTracking skills : empSkillsTracking) {
				long modifiedBy = skills.getModifiedBy();
				long empId1 = skills.getEmpId();
				if (resignedEmpIds.contains(empId1)) {
			        continue; // Skip processing resigned employees
			    }
				EmployeeStatusReport employee = this.employeeRepository.findById(modifiedBy).orElseThrow(() -> new ResourceNotFoundException());
				EmployeeStatusReport employee1 = this.employeeRepository.findById(empId1).orElseThrow();
				skills.setModifiedByName(employee.getEmpName());
				skills.setEmpName(employee1.getEmpName());
				skills.setDateOfJoining(employee1.getDateOfJoining());
				empSkillsTrackings.add(skills);
			}
		}
		if (!empSkillsTrackings.isEmpty()) {

			map.put(ConstantValues.RESULT, empSkillsTrackings);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);

		} else {
			map.put(ConstantValues.RESULT, empSkillsTrackings);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.FAILED_MESSAGE);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
		}

		return map;
	}

	@Override
	public void updateBySkillId(EmpSkillsTracking empSkillsTracking, long skillId) {

		try {
			empSkillsTracking.setModifiedBy(SecurityUtil.getCurrentUserDetails().getEmpId());
			empSkillsTracking.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		} catch (AuthenticationException e) {

			e.printStackTrace();
			throw new ResourceNotProcessedException("SESSION_EXPIRED");
		}

		EmpSkillsTracking empSkillsTracking1 = this.skillsTrackingRepository.findById(skillId).orElseThrow(() -> new ResourceNotFoundException(ConstantValues.USER_NOT_FOUND));
		if (empSkillsTracking1 != null) {

			empSkillsTracking1.setEmpId(empSkillsTracking.getEmpId());
			empSkillsTracking1.setSkills(empSkillsTracking.getSkills());
			empSkillsTracking1.setStartDate(empSkillsTracking.getStartDate());
			empSkillsTracking1.setEndDate(empSkillsTracking.getEndDate());
			empSkillsTracking1.setModifiedBy(empSkillsTracking.getModifiedBy());
			empSkillsTracking1.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			empSkillsTracking1.setWorking(empSkillsTracking.getWorking());
			empSkillsTracking1.setTeam(empSkillsTracking.getTeam());

		}
		this.skillsTrackingRepository.save(empSkillsTracking1);

	}

	@Override
	public ResponseEntity<Map<String, Object>> getAllPresentSkills() {
		List<EmployeeStatusReport> employeeList = employeeRepository.findByIsDeleted(true);
		List<EmpSkillsTracking> empSkillsTrackings = new ArrayList<>();
		if(employeeList!=null) {
			Date date = new Date(System.currentTimeMillis());

			List<EmpSkillsTracking> findAll = this.skillsTrackingRepository.findAll();
			for (EmpSkillsTracking skills : findAll) {

				if (date.after(skills.getStartDate()) &&
						(skills.getEndDate() == null || date.before(skills.getEndDate()))) {
					long modifiedBy = skills.getModifiedBy();
					long empId1 = skills.getEmpId();
					EmployeeStatusReport employee = this.employeeRepository.findById(modifiedBy).orElseThrow(() -> new UserNotFoundException());
					EmployeeStatusReport employee1 = this.employeeRepository.findById(empId1).orElseThrow();
					skills.setModifiedByName(employee.getEmpName());
					skills.setEmpName(employee1.getEmpName());
					skills.setDateOfJoining(employee1.getDateOfJoining());
					empSkillsTrackings.add(skills);
				}
			}
		}
		if (!empSkillsTrackings.isEmpty()) {

			map.put(ConstantValues.RESULT, empSkillsTrackings);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);

		} else {
			map.put(ConstantValues.RESULT, empSkillsTrackings);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.FAILED_MESSAGE);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
		}

		return ResponseEntity.ok(map);
	}
	@Override
	public Map<String, Object> deleteSkill(long skillId) {
		this.skillsTrackingRepository.deleteById(skillId);
		map.put(ConstantValues.RESULT, "deleted Successfully");
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
		return map;
	}

}
