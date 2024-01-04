package com.eidiko.portal.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DateOfBirthDto {

    private long empId;
    private  String empName;
    private LocalDate dateOfBirth;
}