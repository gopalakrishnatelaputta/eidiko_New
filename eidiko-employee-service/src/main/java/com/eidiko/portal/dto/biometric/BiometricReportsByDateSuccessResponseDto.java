package com.eidiko.portal.dto.biometric;

import java.sql.Date;
import java.sql.Timestamp;

public class BiometricReportsByDateSuccessResponseDto {
	private Long biometricReportId;
	private Long empId;
	private Date biometricDate;
	private Timestamp checkInTime;
	private Timestamp checkOutTime;
	private String totalWorkingTime;
	private Boolean isLate;
	private Timestamp modifiedOn;
	private String month;
	private int year;
	
}
