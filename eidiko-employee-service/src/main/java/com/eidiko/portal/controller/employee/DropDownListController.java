package com.eidiko.portal.controller.employee;

import com.eidiko.portal.service.employee.DropDownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/drop-down-list")
public class DropDownListController {

	@Autowired
	private DropDownService dropDownService;

	@GetMapping("/client-locations")
	public ResponseEntity<Map<String, Object>> getClientLocation() {

		Map<String, Object> result = this.dropDownService.getClientLocations();

		return ResponseEntity.ok().body(result);
	}

	@GetMapping("/get-skills")
	public ResponseEntity<Map<String, Object>> getTeams() {

		Map<String, Object> result = this.dropDownService.getTeams();

		return ResponseEntity.ok().body(result);
	}

	@GetMapping("/get-teams")
	public ResponseEntity<Map<String, Object>> getSkills() {

		Map<String, Object> result = this.dropDownService.getSkills();

		return ResponseEntity.ok().body(result);
	}

	@GetMapping("/get-access-levels")
	public ResponseEntity<Map<String, Object>> getAccessLevels() {

		Map<String, Object> result = this.dropDownService.getAccessLevel();

		return ResponseEntity.ok().body(result);
	}
	
	@GetMapping("/get-all-designations")
	public ResponseEntity<Map<String, Object>> getDesignations()
	{
		Map<String,Object> designations = this.dropDownService.getAllDesignations();
		return ResponseEntity.ok().body(designations);
	}
}
