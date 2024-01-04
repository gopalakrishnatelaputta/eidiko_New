package com.eidiko.portal.entities.leavesbeta;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class LeaveTypesBeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long leaveTypeId;
    private String leaveCode;
    private String leaveName;
    private Long createdBy;
    @CreationTimestamp
    private Timestamp createdDate;

}