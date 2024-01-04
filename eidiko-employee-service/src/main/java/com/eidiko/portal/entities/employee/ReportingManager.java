package com.eidiko.portal.entities.employee;

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
public class ReportingManager  {


	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long reportingManagerId;
	private Timestamp modifiedDate;
	private Date startDate;
	private Date endDate;
	private long modifiedBy;
	
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EMP_ID")
	private Employee employee;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "managerId")
	private Employee manager;


	
	
	
}
