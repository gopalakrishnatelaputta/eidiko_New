package com.eidiko.portal.entities.biometric;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;



@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="employee")
public class EmployeeBiometric {

	
	@Id
	@Column(name = "EMP_ID")
	private long empId;
	private String empName;
	private String emailId;
	private Date dateOfJoining;
	private Timestamp modifiedDate;
	private String contactNo;
	private long createdBy;
	private boolean isDeleted;
	private String status;

	private String about;


	@JsonIgnore
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<EmpShiftTimingsBiometric> empShiftTimings = new HashSet<>();


	
	
	
}
