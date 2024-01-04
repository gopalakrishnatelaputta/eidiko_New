package com.eidiko.portal.entities.taskstatus;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "task_status_exemption_emp")
public class TaskStatusExemptionEmp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TSE_EMP_ID")
    private Long tseEmpId;

    private LocalDate startDate;
    private LocalDate endDate;
    private Timestamp modifiedOn;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODIFIED_BY")
    private EmployeeStatusReport modifiedBy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EMP_ID")
    private EmployeeStatusReport employee;


}
