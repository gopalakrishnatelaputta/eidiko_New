package com.eidiko.portal.entities.reportingmanager;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "emp_warning_mails")
public class CautionMail
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long warningMailId;
    
    @Column(name = "subject")
    private String title;
    private String description;
    private Long warnedBy;
    private Timestamp sentDate;
    @Column(name = "emp_id")
    private long empId;
    private Date warningDate;
    private String severityLevel;
    
    private String warningLevel;
    

}
