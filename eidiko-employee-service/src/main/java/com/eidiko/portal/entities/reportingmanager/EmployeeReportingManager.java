package com.eidiko.portal.entities.reportingmanager;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.sql.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "Employee")
public class EmployeeReportingManager {

	@Id
	@Column(name = "emp_id")
	private long empId;
	private String empName;
	private String emailId;
	private String contactNo;
	private Date dateOfJoining;

}
