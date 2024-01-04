package com.eidiko.portal.entities.leaves;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeLeaveAuditStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long eAuditLeaveId;
    private long empId;
    private long updatedBy;
    private Timestamp updatedOn;
    private String description;
    private String payload;



}
