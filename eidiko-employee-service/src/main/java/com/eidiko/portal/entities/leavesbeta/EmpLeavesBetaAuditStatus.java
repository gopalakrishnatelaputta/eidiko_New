package com.eidiko.portal.entities.leavesbeta;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "emp_leaves_beta_audit_status")
public class EmpLeavesBetaAuditStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long leavesBetaAuditId;
    private String payLoad;
    @CreationTimestamp
    private Timestamp updatedDate;
    private String status;
    private long empId;
    private String description;
    private long updatedBy;
}