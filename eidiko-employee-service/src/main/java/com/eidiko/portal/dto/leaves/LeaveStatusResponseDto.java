package com.eidiko.portal.dto.leaves;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveStatusResponseDto {

	
	private long empId;
	private String empName;
	private int leavesCount;
	private int absentCount;
	private int total;
	private String band;
	private int totalLeavesAsPerband;
	
	
}
