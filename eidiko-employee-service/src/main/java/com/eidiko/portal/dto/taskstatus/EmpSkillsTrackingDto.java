package com.eidiko.portal.dto.taskstatus;

import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmpSkillsTrackingDto {

	
	private long skillId;
    private long empId;
    private String empName;
    private Date startDate;
    private Date endDate;
    private String working;
    private String skills;
    private String team;
    private Timestamp modifiedOn;
    private long modifiedBy;
    private String modifiedByName;
}
