package com.eidiko.portal.dto.leaves;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeLeavesDto {

    private Date date;
    private char status;
}
