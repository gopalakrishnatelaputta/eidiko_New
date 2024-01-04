package com.eidiko.portal.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportingManagerDto {

	private long reportingManagerId;
	private Timestamp modifiedDate;
	private Date startDate;
	private Date endDate;
	
	
	private long empId;
	private String empName;
	
	
}
