package com.eidiko.portal.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeBandDesgReqDto {

	private long empId;
	private int bandId;
	private String desgName;



}
