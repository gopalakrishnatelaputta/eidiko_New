package com.eidiko.portal.dto.taskstatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EmployeeDto {

    private String empName;
    private String emailId;
    private String contactNo;
    private long empId;

}
