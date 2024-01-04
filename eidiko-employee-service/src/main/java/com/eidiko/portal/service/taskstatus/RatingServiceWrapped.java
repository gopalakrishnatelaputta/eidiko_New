package com.eidiko.portal.service.taskstatus;

import com.eidiko.portal.entities.taskstatus.EmpReviewRating;

import java.util.Map;


public interface RatingServiceWrapped {
	
	 void insert(EmpReviewRating empReviewRating);
	
	 Map<String, Object> getByEmpIdAndYear(long empId, int year);
	 
	 Map<String, Object> getByEmpIdAndMonthAndYear(long empId,int month, int year);
	 
	 Map<String, Object> getAllByMonthAndYear(int month, int year);
	
	
	Map<String, Object> getAllRatingsByYear(int year);
	
	Map<String, Object> deleteRating(long empRatingId);

	Map<String, Object> updateRating(long empRatingId, EmpReviewRating empReviewRating);

	
}
