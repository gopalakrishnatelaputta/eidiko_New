package com.eidiko.portal.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpAccessLevelResDto {
	private int accessLvlId;
	private String accessLvlName;
	private String accessLvlDesc;
}
