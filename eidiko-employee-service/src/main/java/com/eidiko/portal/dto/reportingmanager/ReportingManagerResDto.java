package com.eidiko.portal.dto.reportingmanager;

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
public class ReportingManagerResDto {

	private long reportingManagerId;
	private Timestamp modifiedDate;
	private Date startDate;
	private Date endDate;
	private long modifiedBy;

	private long empId;
	private String empName;

	private long managerId;
	private String managerName;
	
	
}
