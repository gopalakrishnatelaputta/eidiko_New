package com.eidiko.portal.dto.taskstatus;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaskExemptionResponseDto {

    private Long tseEmpId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Timestamp modifiedOn;
    private long modifiedBy;
    private Long empId;
}
