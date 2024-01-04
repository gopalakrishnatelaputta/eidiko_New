package com.eidiko.portal.dto.taskstatus;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaskExemptionReqDto {

    private Long tseEmpId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Timestamp modifiedOn;
    private long modifiedBy;
    private List<Long> empId;
}
