package com.eidiko.portal.dto.taskstatus;

import lombok.*;

import java.sql.Timestamp;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class RatingDto {

	
	private long empRatingId;
    private long empId;
    private int month;
    private int year;
    private String technology;
    private double technicalRating;
    private double communicationRating;
    private String remarks;
    private long reviewedBy;
    private long modifiedBy;
    private Timestamp modifiedOn;
}
