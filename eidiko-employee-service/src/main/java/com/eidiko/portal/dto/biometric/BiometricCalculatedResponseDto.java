package com.eidiko.portal.dto.biometric;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BiometricCalculatedResponseDto {

	
	private long empId;
	private String empName;
	private long lateCount;
	private long nonLateCount;
	private long greaterThanWrkHrsCount;
	private long lessThanWrkHrsCount;
	private long veryLateCount;
	private long avgworkingMinutes;
	
	
	
}
