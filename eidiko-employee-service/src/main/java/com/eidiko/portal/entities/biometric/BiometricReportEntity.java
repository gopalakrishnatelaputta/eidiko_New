package com.eidiko.portal.entities.biometric;

import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "emp_biometric_report")
public class BiometricReportEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "biometricReportId")
	private Long biometricReportId;

	@Column(name = "EMP_ID")
	private Long empId;
	@Column(name = "biometricDate")
	private Date biometricDate;
	@Column(name = "checkinTime")
	private Timestamp checkInTime;
	@Column(name = "checkoutTime")
	private Timestamp checkOutTime;
	@Column(name = "totalWorkingTime")
	private String totalWorkingTime;
	@Column(name = "isLate")
	private Boolean isLate;
	@Column(name = "modifiedOn")
	private Timestamp modifiedOn;
	@Column(name = "month")
	private String month;
	@Column(name = "year")
	private int year;
	@Column(name="isVeryLate")
	private boolean isVeryLate;

	public BiometricReportEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BiometricReportEntity(Long biometricReportId, Long empId, Date biometricDate, Timestamp checkInTime,
			Timestamp checkOutTime, String totalWorkingTime, Boolean isLate, Timestamp modifiedOn, String month,
			int year) {
		super();
		this.biometricReportId = biometricReportId;
		this.empId = empId;
		this.biometricDate = biometricDate;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
		this.totalWorkingTime = totalWorkingTime;
		this.isLate = isLate;
		this.modifiedOn = modifiedOn;
		this.month = month;
		this.year = year;
	}

	public Long getBiometricReportId() {
		return biometricReportId;
	}

	public void setBiometricReportId(Long biometricReportId) {
		this.biometricReportId = biometricReportId;
	}

	public Long getEmpId() {
		return empId;
	}

	public void setEmpId(Long empId) {
		this.empId = empId;
	}

	public Date getBiometricDate() {
		return biometricDate;
	}

	public void setBiometricDate(Date biometricDate) {
		this.biometricDate = biometricDate;
	}

	public Timestamp getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(Timestamp checkInTime) {
		this.checkInTime = checkInTime;
	}

	public Timestamp getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(Timestamp checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

	public String getTotalWorkingTime() {
		return totalWorkingTime;
	}

	public void setTotalWorkingTime(String totalWorkingTime) {
		this.totalWorkingTime = totalWorkingTime;
	}

	public Boolean getIsLate() {
		return isLate;
	}

	public void setIsLate(Boolean isLate) {
		this.isLate = isLate;
	}

	public Timestamp getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	
	
	public boolean isVeryLate() {
		return isVeryLate;
	}

	public void setVeryLate(boolean isVeryLate) {
		this.isVeryLate = isVeryLate;
	}

	@Override
	public String toString() {
		return "BiometricReportEntity [biometricReportId=" + biometricReportId + ", empId=" + empId + ", biometricDate="
				+ biometricDate + ", checkInTime=" + checkInTime + ", checkOutTime=" + checkOutTime
				+ ", totalWorkingTime=" + totalWorkingTime + ", isLate=" + isLate + ", modifiedOn=" + modifiedOn
				+ ", month=" + month + ", year=" + year + "]";
	}

}
