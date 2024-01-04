package com.eidiko.portal.service.taskstatus;

import com.eidiko.portal.entities.taskstatus.EmpSkillsTracking;
import org.springframework.http.ResponseEntity;

import javax.security.sasl.AuthenticationException;
import java.util.Map;


public interface EmpSkillsTrackingServiceWrapped {

	
	void insert(EmpSkillsTracking empSkillsTracking)throws AuthenticationException;
	Map<String, Object> getSkillByEmpId(long empId);
	Map<String, Object> getBySkillAndWorking(String skill, String working);
	void updateBySkillId(EmpSkillsTracking empSkillsTracking, long skillId);
	ResponseEntity<Map<String, Object>> getAllPresentSkills();
	Map<String, Object> deleteSkill(long skillId);
}
