package com.eidiko.portal.dto.leaves;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveStatusDto {

	
	private String empId;
	private String year;
	private String month;
	private String day;
	private String status;
	private int count;
	
}
