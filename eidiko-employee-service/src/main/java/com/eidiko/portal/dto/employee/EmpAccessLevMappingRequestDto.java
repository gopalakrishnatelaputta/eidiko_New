package com.eidiko.portal.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpAccessLevMappingRequestDto {

    private int accessLvlId;
    private  Long empId;
}
