package com.eidiko.portal.dto.taskstatus;

import com.eidiko.portal.entities.taskstatus.EmployeeStatusReport;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DailyStatusReportDto {

    private long taskDetailsId;
    @NotBlank(message = "task details are mandatory")
    private String taskDetails;
    @NotBlank(message = "description is mandatory")
    @Pattern(regexp = "^.{10,}$" , message = "Description must contain more than are equal to 10 characters")
    private String desc;
    @Min(value = 1,message = "assigned by should contain only numbers")
    private long taskAssignedBy;
    private EmployeeStatusReport assignedBy;
    @NotBlank(message = "status field is mandatory")
    private String status;
    private String reason;

    private EmployeeStatusReport verifiedBy;
    private long taskVerifiedBy;
    private Timestamp statusReportDate;
    @NotBlank(message = "Team field is mandatory")
    private String team;

    @Column(name = "ASSIGNED_DATE")
    private LocalDate assignedDate;

}
