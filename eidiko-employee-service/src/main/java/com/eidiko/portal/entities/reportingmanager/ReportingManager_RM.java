package com.eidiko.portal.entities.reportingmanager;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reporting_manager")
public class ReportingManager_RM {

	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long reportingManagerId;
	private Timestamp modifiedDate;
	private Date startDate;
	private Date endDate;
	private long modifiedBy;
	
@Column(name = "emp_id")
	private long empId;
	

	private long managerId;

	
}
