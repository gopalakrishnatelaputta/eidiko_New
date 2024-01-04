package com.eidiko.portal.dto.employee;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
public class BiometricReportsDto {
	private Long biometricReportId;
	private Long empId;
	private Date biometricDate;
	private Timestamp checkInTime;
	private Timestamp checkOutTime;
	private int totalWorkingTime;
	private Boolean isLate;
	private Timestamp modifiedOn;
	private int month;
	private int year;
	
	@Override
	public String toString() {
		return "BiometricReportsDto [biometricReportId=" + biometricReportId + ", empId=" + empId + ", biometricDate="
				+ biometricDate + ", checkInTime=" + checkInTime + ", checkOutTime=" + checkOutTime
				+ ", totalWorkingTime=" + totalWorkingTime + ", isLate=" + isLate + ", modifiedOn=" + modifiedOn
				+ ", month=" + month + ", year=" + year + "]";
	}
	
}
