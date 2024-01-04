package com.eidiko.portal.dto.taskstatus;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DailyStatusReportResponseDto {


private long taskDetailsId;

private String taskDetail;
private String desc;
private String status;
private String reason;
private Timestamp statusReportDate;
private String team;
private LocalDate assignedDate;

private long empId;
private long assignedBy;
private String assignedByName;

private Long verifiedBy;
private String verifiedByName;


}
