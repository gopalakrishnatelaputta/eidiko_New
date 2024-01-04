package com.eidiko.portal.dto.leaves;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HolidayDto {
    private Date holidayDate;
    private String holidayDesc;
}
