package com.eidiko.portal.entities.taskstatus;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "emp_low_rating_audit_status")
public class EmpLowRatingAudit
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long empLowRatingId;
    private long empId;
    private int month;
    private LocalDate updatedOn;
}

