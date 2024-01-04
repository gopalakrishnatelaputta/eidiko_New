package com.eidiko.portal.entities.biometric;

import jakarta.persistence.*;

import java.sql.Timestamp;



@Entity
@Table(name = "employee_biometric")
public class


BiometricEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "biometricId")
	private Long biometricId;
	
	@Column(name = "EMP_ID")
	private Long empId;
	
	
	@Column(name = "bioDate")
	private Timestamp bioDate;
	
	/*
	 * @Transient private LocalDate fromDate;
	 * 
	 * @Transient private LocalDate toDate;
	 * 
	 * public void setfromDate(LocalDate fromDate) { this.fromDate = fromDate; }
	 * public LocalDate getfromDate() { return fromDate; }
	 * 
	 * public void settoDate(LocalDate toDate) { this.toDate = toDate; } public
	 * LocalDate gettoDate() { return toDate; }
	 */
	
	
	
	public BiometricEntity() {
		super();
	}
	public BiometricEntity(Long biometricId, Long empId, Timestamp bioDate) {
		super();
		this.biometricId = biometricId;
		this.empId = empId;
		this.bioDate = bioDate;
	}
	public Long getBiometricId() {
		return biometricId;
	}
	public void setBiometricId(Long biometricId) {
		this.biometricId = biometricId;
	}
	public Long getEmpId() {
		return empId;
	}
	public void setEmpId(Long empId) {
		this.empId = empId;
	}
	public Timestamp getBioDate() {
		return bioDate;
	}
	public void setBioDate(Timestamp bioDate) {
		this.bioDate = bioDate;
	}
	@Override
	public String toString() {
		return "BiometricEntity [biometricId=" + biometricId + ", empId=" + empId + ", bioDate=" + bioDate + "]";
	}
	
	

}
