package com.eidiko.portal.dto.employee;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftTimingReqDto {

	private long shiftTimingId;
    private long empId;
    
   
    private List<@Pattern(regexp = "^[1-7]", message = "days must be in 1 to 7. e,g: 1(SUNDAY),2(MONDAY).... etc") String> weekOff;
    @NotNull
    private Date startDate;
    private Date endDate;
    @NotNull
    private Time shiftStartTime;
    @NotNull
    private Time shiftEndTime;



}
