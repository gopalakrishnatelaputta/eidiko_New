package com.eidiko.portal.entities.taskstatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "emp_skills_tracking")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmpSkillsTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long skillId;
    private long empId;
    @Transient
    private String empName;
    private Date startDate;
    private Date endDate;
    private String working;
    private String skills;
    @Transient
    private Date dateOfJoining;
    private String team;
    private Timestamp modifiedOn;
    private long modifiedBy;
    @Transient
    private String modifiedByName;
}
