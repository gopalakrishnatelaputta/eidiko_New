package com.eidiko.portal.entities.biometric;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="biometric_exemption_emp")
public class BiometricExemptionEmp {

	@Id
	private long beEmpId;
	@Column(name = "EMP_ID")
	private long empId;
	private boolean isBiometricEnabled;
	
	
}
