package com.eidiko.portal.entities.taskstatus;

import com.eidiko.portal.helper.taskstatus.ConstantValues;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "emp_review_rating")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmpReviewRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long empRatingId;
    private long empId;
    @Transient
    private String empName;
    private int month;
    private int year;
    private String technology;
    @Digits(integer = 10, fraction = 2, message = ConstantValues.RATING)
    private double technicalRating;
    @Digits(integer = 10, fraction = 2, message = ConstantValues.RATING)
    private double communicationRating;
    private String remarks;
    private long reviewedBy;
    @Transient
    private String reviewedByName;
    private long modifiedBy;
    private Timestamp modifiedOn;

}
