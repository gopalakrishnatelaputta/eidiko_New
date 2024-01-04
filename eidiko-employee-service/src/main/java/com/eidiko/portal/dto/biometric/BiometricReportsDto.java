package com.eidiko.portal.dto.biometric;

import lombok.*;

import java.sql.Timestamp;
import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
	private boolean isVerylate;
	
	

}
