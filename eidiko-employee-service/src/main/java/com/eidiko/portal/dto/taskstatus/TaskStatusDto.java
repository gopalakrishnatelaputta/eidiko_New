package com.eidiko.portal.dto.taskstatus;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskStatusDto {

	 	private long taskDetailsId;
	    private String taskDetails;
	    private String desc;
	    private long taskAssignedBy;
	    private String status;
	    private String reason;
	    private long taskVerifiedBy;
	    private Timestamp statusReportDate;
	    private String team;
	    private LocalDate assignedDate;
	
}
