package com.eidiko.portal.dto.employee;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManagerRequestDto {

	private long reportingManagerId;
    private long empId;
    private long managerId;
    private Date startDate;
    private Date endDate;
}
