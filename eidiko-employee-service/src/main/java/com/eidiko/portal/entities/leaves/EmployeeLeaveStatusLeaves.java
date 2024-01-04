package com.eidiko.portal.entities.leaves;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "employee_leave_status")
public class EmployeeLeaveStatusLeaves {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@Column(name = "leave_status_id")
	private long leaveStatusId;
	@Column(name = "emp_id")
	private long empId;
	//@Column(name = "leave_date")
	private Date leaveDate;
	//@Column(name = "e_status")
	private char eStatus;
	//@Column(name = "modified_by")
	private long modifiedBy;
	
	@Transient
	private String modifiedByName;
	
	
	
}
