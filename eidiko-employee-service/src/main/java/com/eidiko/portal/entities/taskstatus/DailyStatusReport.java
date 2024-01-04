package com.eidiko.portal.entities.taskstatus;

import com.eidiko.portal.dto.taskstatus.DailyStatusReportDto;
import jakarta.persistence.*;
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
public class DailyStatusReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TASK_DETAILS_ID")
    private long taskDetailsId;
    @Column(name ="TASK_DETAILS")
    private String taskDetails;
    @Column(name = "`DESC`")
    private String desc;

    @JoinColumn(name = "ASSIGNED_BY")
    @ManyToOne(fetch = FetchType.LAZY)
    private EmployeeStatusReport assignedBy;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "REASON")
    private String reason;

    @JoinColumn(name = "VERIFIED_BY")
    @ManyToOne(fetch = FetchType.LAZY)
    private EmployeeStatusReport verifiedBy;
    @Column(name = "STATUS_REPORT_DATE")
    private Timestamp statusReportDate;
    @Column(name = "TEAM")
    private String team;
    @Column(name = "ASSIGNED_DATE")
    private LocalDate assignedDate;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID")
    private EmployeeStatusReport employee;




    public DailyStatusReport(DailyStatusReportDto dailyStatusReportDto) {
        this.setTaskDetailsId(dailyStatusReportDto.getTaskDetailsId());
        this.taskDetails=dailyStatusReportDto.getTaskDetails();
        this.desc=dailyStatusReportDto.getDesc();
        this.statusReportDate=dailyStatusReportDto.getStatusReportDate();
        this.status=dailyStatusReportDto.getStatus();
        this.team=dailyStatusReportDto.getTeam();
        this.assignedBy=dailyStatusReportDto.getAssignedBy();
        this.reason=dailyStatusReportDto.getReason();
        this.verifiedBy=dailyStatusReportDto.getVerifiedBy();
        this.assignedDate= dailyStatusReportDto.getAssignedDate();
    }

}
