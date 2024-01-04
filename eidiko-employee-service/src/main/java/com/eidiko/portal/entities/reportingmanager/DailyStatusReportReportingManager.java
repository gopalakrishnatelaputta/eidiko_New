package com.eidiko.portal.entities.reportingmanager;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DAILY_STATUS_REPORT")
public class DailyStatusReportReportingManager {
    @Id

    @Column(name = "TASK_DETAILS_ID")
    private long taskDetailsId;
    @Column(name ="TASK_DETAILS")
    private String taskDetails;
    @Column(name = "`DESC`")
    private String desc;

    @Column(name = "assigned_by")
    private long assignedBy;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "REASON")
    private String reason;

 
    //private long verifiedBy;
    @Column(name = "STATUS_REPORT_DATE")
    private Timestamp statusReportDate;
    @Column(name = "TEAM")
    private String team;
    @Column(name = "ASSIGNED_DATE")
    private LocalDate assignedDate;

  
  @Column(name = "emp_id")
  private long empId;
}
