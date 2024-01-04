package com.eidiko.portal.entities.biometric;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "EmployeeLeaveStatus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLeaveStatus {

	@Id
	private long leaveStatusId;
	private Date leaveDate;
	private char eStatus;
	@Column(name = "EMP_ID")
	private long empId;
	private long modifiedBy;
	
	
}
