package com.eidiko.portal.dto.reportingmanager;

import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CautionMailResponseDto {

	private Long warningMailId;
	private String title;
	private String description;
	private Long warnedBy;
	private Timestamp sentDate;
	private long empId;

	private Date warningDate;
	private String severityLevel;
	private String warningLevel;

	private String empName;
	private String warnedByName;

}
