package com.eidiko.portal.controller.taskstatus;

import com.eidiko.portal.config.employee.SecurityUtil;
import com.eidiko.portal.entities.taskstatus.EmpReviewRating;
import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import com.eidiko.portal.helper.employee.AuthAssignedConstants;
import com.eidiko.portal.helper.employee.Helper;
import com.eidiko.portal.helper.taskstatus.ConstantValues;
import com.eidiko.portal.service.taskstatus.RatingServiceWrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rating")
public class RatingControllerWrapped {
	

	@Autowired
	private Helper helper;
	
	@Autowired
	RatingServiceWrapped ratingService;
	
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
	//HR
	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@PostMapping("/insertRating")
	public Map<String, Object> insertRating(@RequestBody EmpReviewRating empReviewRating)
			throws URISyntaxException, JsonMappingException, JsonProcessingException {
		Map<String,Object> map = new HashMap<>();
		this.ratingService.insert(empReviewRating);
		map.put(ConstantValues.MESSAGE , ConstantValues.CREATED);
		map.put(ConstantValues.STATUS_TEXT , HttpStatus.CREATED.value());
		map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
		return map;

	}
	//EMP
	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@GetMapping("/get-ratings-by/{empId}/{year}")
	public Map<String, Object> getRatingsByEmpIdAndYear(@PathVariable long empId,@PathVariable int year) throws JsonProcessingException {		
		if (!validateUser(empId)) {
			throw new ResourceNotProcessedException("You don't have Authority");
		}

		return this.ratingService.getByEmpIdAndYear(empId,year);
	}

	//EMP
	@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
	@GetMapping("/get-ratings-by/{empId}/{month}/{year}")
	public Map<String, Object> getRatingsByEmpIdAndMonthAndYear(@PathVariable long empId,@PathVariable int month,@PathVariable int year
			) throws JsonProcessingException {		
		if (!validateUser(empId)) {
			throw new ResourceNotProcessedException("You don't have Authority");
		}

		return this.ratingService.getByEmpIdAndMonthAndYear(empId,month,year);
	}
	//HR
	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@GetMapping("/get-ratings-by-month-year/{month}/{year}")
	public Map<String, Object> getRatingsByMonthAndYear(@PathVariable int month,@PathVariable int year) throws JsonProcessingException {		

		return this.ratingService.getAllByMonthAndYear(month,year);
	}
	//HR
	@PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
	@GetMapping("/get-ratings-by-year/{year}")
	public Map<String, Object> getRatingsByYear(@PathVariable int year) throws JsonProcessingException {		

		return this.ratingService.getAllRatingsByYear(year);
	}
	
	
	@DeleteMapping("/delete-rating/{empRatingId}")
    public  Map<String,Object> deleteRating(@PathVariable long empRatingId)
	 {
		 return this.ratingService.deleteRating(empRatingId);
	 }
	@PutMapping("/update-rating/{empRatingId}")
	public  Map<String,Object> updateRating(@PathVariable long empRatingId,@RequestBody EmpReviewRating empReviewRating)
	{
		return this.ratingService.updateRating(empRatingId,empReviewRating);
	}
	
	

}
