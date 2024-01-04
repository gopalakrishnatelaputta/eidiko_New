package com.eidiko.portal.serviceimpl.taskstatusimpl;

import com.eidiko.portal.entities.employee.ResignedEmployee;
import com.eidiko.portal.entities.taskstatus.EmpReviewRating;
import com.eidiko.portal.entities.taskstatus.EmployeeStatusReport;
import com.eidiko.portal.exception.taskstatus.UserNotFoundException;
import com.eidiko.portal.helper.taskstatus.ConstantValues;
import com.eidiko.portal.repo.employee.ResignedEmpRepo;
import com.eidiko.portal.repo.taskstatus.EmployeeRepository;
import com.eidiko.portal.repo.taskstatus.RatingRepository;
import com.eidiko.portal.service.taskstatus.RatingServiceWrapped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RatingServiceImplWrapped implements RatingServiceWrapped{

	@Autowired
	private RatingRepository ratingRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
			private ResignedEmpRepo resignedEmpRepo;

	Map<String, Object> map = new HashMap<>();

	@Override
	public Map<String, Object> getAllRatingsByYear(int year) {
		List<EmpReviewRating> empReviewRatings = this.ratingRepository.findAllByYear(year);
		List<EmpReviewRating> empReviewRating= new ArrayList<>();
		List<ResignedEmployee> resgnEmpList = resignedEmpRepo.findAll();
		Set<Long> resignedEmpIds = resgnEmpList.stream()
				.map(ResignedEmployee::getEmpId)
				.collect(Collectors.toSet());
		for (EmpReviewRating rating : empReviewRatings) {
			long reviewedBy = rating.getReviewedBy();
			long empId = rating.getEmpId();
			EmployeeStatusReport employee = this.employeeRepository.findById(reviewedBy).orElseThrow(() -> new UserNotFoundException());
			EmployeeStatusReport employee1 = this.employeeRepository.findById(empId).orElseThrow();
			rating.setReviewedByName(employee.getEmpName());
			rating.setEmpName(employee1.getEmpName());
			if (resignedEmpIds.contains(empId)) {
				continue; // Skip processing resigned employees
			}
			empReviewRating.add(rating);
		}


		if (!empReviewRatings.isEmpty()) {

			map.put(ConstantValues.RESULT, empReviewRating);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);

		} else {
			map.put(ConstantValues.RESULT, empReviewRatings);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.FAILED_MESSAGE);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
		}

		return map;
	}

	@Override
	public Map<String, Object> getByEmpIdAndYear(long empId, int year) {
		List<EmpReviewRating> empReviewRatings = this.ratingRepository.findAllByEmpIdAndYear(empId, year);
		List<EmpReviewRating> empReviewRating= new ArrayList<>();
		for (EmpReviewRating rating : empReviewRatings) {
			long reviewedBy = rating.getReviewedBy();
			long empId1 = rating.getEmpId();
			EmployeeStatusReport employee = this.employeeRepository.findById(reviewedBy).orElseThrow(() -> new UserNotFoundException());
			EmployeeStatusReport employee1 = this.employeeRepository.findById(empId1).orElseThrow();
			rating.setReviewedByName(employee.getEmpName());
			rating.setEmpName(employee1.getEmpName());
			empReviewRating.add(rating);
		}

		if (!empReviewRatings.isEmpty()) {

			map.put(ConstantValues.RESULT, empReviewRating);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);

		} else {
			map.put(ConstantValues.RESULT, empReviewRatings);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.FAILED_MESSAGE);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
		}

		return map;
	}

	@Override
	public Map<String, Object> getByEmpIdAndMonthAndYear(long empId, int month ,int year) {
		List<EmpReviewRating> empReviewRatings = ratingRepository.findAllByEmpIdAndMonthAndYear(empId, month, year);

		List<EmpReviewRating> empReviewRating= new ArrayList<>();
		for (EmpReviewRating rating : empReviewRatings) {
			long reviewedBy = rating.getReviewedBy();
			long empId1 = rating.getEmpId();
			EmployeeStatusReport employee = this.employeeRepository.findById(reviewedBy).orElseThrow(() -> new UserNotFoundException());
			EmployeeStatusReport employee1 = this.employeeRepository.findById(empId1).orElseThrow();
			rating.setReviewedByName(employee.getEmpName());
			rating.setEmpName(employee1.getEmpName());
			empReviewRating.add(rating);
		}
		if (!empReviewRatings.isEmpty()) {

			map.put(ConstantValues.RESULT, empReviewRating);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);

		} else {
			map.put(ConstantValues.RESULT, empReviewRatings);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.FAILED_MESSAGE);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
		}

		return map;
	}

	@Override
	public Map<String, Object> getAllByMonthAndYear(int month, int year) {
		List<EmpReviewRating> empReviewRatings = this.ratingRepository.findAllByMonthAndYear( month, year);
		List<EmpReviewRating> empReviewRating= new ArrayList<>();
		List<ResignedEmployee> resgnEmpList = resignedEmpRepo.findAll();
		Set<Long> resignedEmpIds = resgnEmpList.stream()
				.map(ResignedEmployee::getEmpId)
				.collect(Collectors.toSet());
		for (EmpReviewRating rating : empReviewRatings) {
			long reviewedBy = rating.getReviewedBy();
			long empId = rating.getEmpId();
			EmployeeStatusReport employee = this.employeeRepository.findById(reviewedBy).orElseThrow(() -> new UserNotFoundException());
			EmployeeStatusReport employee1 = this.employeeRepository.findById(empId).orElseThrow();
			rating.setReviewedByName(employee.getEmpName());
			rating.setEmpName(employee1.getEmpName());
			if (resignedEmpIds.contains(empId)) {
				continue;
			}
			empReviewRating.add(rating);
		}


		if (!empReviewRatings.isEmpty()) {

			map.put(ConstantValues.RESULT, empReviewRating);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);

		} else {
			map.put(ConstantValues.RESULT, empReviewRatings);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.MESSAGE, ConstantValues.FAILED_MESSAGE);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
		}

		return map;
	}

	@Override
	public void insert(EmpReviewRating empReviewRating) {
		EmployeeStatusReport employee=this.employeeRepository.findById(empReviewRating.getEmpId()).orElseThrow(()->new UserNotFoundException(ConstantValues.USER_NOT_FOUND + ConstantValues.WITH_GIVEN_ID + empReviewRating.getEmpId()));
		EmployeeStatusReport reviewedBy=this.employeeRepository.findById(empReviewRating.getReviewedBy()).orElseThrow(()->new UserNotFoundException(ConstantValues.USER_NOT_FOUND + ConstantValues.WITH_GIVEN_ID + empReviewRating.getReviewedBy()));
		if(employee!=null && reviewedBy!=null) {
			empReviewRating.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			ratingRepository.save(empReviewRating);
		}
}
	
	public Map<String, Object> deleteRating(long empRatingId)
	{
		 this.ratingRepository.deleteById(empRatingId);
		map.put(ConstantValues.RESULT, "deleted Successfully");
		map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
		return map;
	}



	public Map<String, Object> updateRating(long empRatingId,EmpReviewRating empReviewRating)
	{
		EmpReviewRating rating = this.ratingRepository.findById(empRatingId).orElseThrow(()->new UserNotFoundException(ConstantValues.USER_NOT_FOUND));
		if(rating!=null)
		{
			rating.setEmpId(empReviewRating.getEmpId());
			rating.setMonth(empReviewRating.getMonth());
			rating.setYear(empReviewRating.getYear());
			rating.setTechnology(empReviewRating.getTechnology());
			rating.setCommunicationRating(empReviewRating.getCommunicationRating());
			rating.setTechnicalRating(empReviewRating.getTechnicalRating());
			rating.setRemarks(empReviewRating.getRemarks());
			rating.setReviewedBy(empReviewRating.getReviewedBy());
			rating.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			rating.setModifiedBy(empReviewRating.getModifiedBy());
			ratingRepository.save(rating);
			map.put(ConstantValues.RESULT, ConstantValues.UPDATED);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);

		}
		return map;
	}
}