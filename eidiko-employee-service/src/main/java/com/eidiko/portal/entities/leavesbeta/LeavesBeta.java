package com.eidiko.portal.entities.leavesbeta;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LeavesBeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long leavesBetaId;
    private long empId;
    private String leaveType;
    private LocalDate leaveDate;
    private String reason;
    private String status;
    private String attachments;
    private long createdBy;
    @CreationTimestamp
    private Timestamp createdDate;
    private long leaveId;
    @Transient
    private long count;
    private boolean isHalfDay;
    private Long modifiedBy;
    private String comments;
}