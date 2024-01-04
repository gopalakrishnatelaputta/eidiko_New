package com.eidiko.portal.entities.biometric;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpBiometricMissingReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long empBioMissingId;
	
	@Column(name = "EMP_ID")
	private long empId;
	private Date missingDate;
	private int missingMonth;
	private int missingYear;
	private long modifiedBy;
	
	
	@Transient
	private String empName;
	
	
}
