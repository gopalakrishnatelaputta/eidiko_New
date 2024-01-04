package com.eidiko.portal.dto.leaves;

import java.sql.Date;

public class EmployeeLeaveStatusDto {

	private long leaveStatusId;
	private long empId;
	private Date leaveDate;
	private String eStatus;
	private long modifiedBy;

	public EmployeeLeaveStatusDto(long leaveStatusId, long empId, Date leaveDate, String eStatus, long modifiedBy) {
		super();
		this.leaveStatusId = leaveStatusId;
		this.empId = empId;
		this.leaveDate = leaveDate;
		this.eStatus = eStatus;
		this.modifiedBy = modifiedBy;
	}

	public EmployeeLeaveStatusDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public long getLeaveStatusId() {
		return leaveStatusId;
	}

	public void setLeaveStatusId(long leaveStatusId) {
		this.leaveStatusId = leaveStatusId;
	}

	public long getEmpId() {
		return empId;
	}

	public void setEmpId(long empId) {
		this.empId = empId;
	}

	public Date getLeaveDate() {
		return leaveDate;
	}

	public void setLeaveDate(Date leaveDate) {
		this.leaveDate = leaveDate;
	}

	public String geteStatus() {
		return eStatus;
	}

	public void seteStatus(String eStatus) {
		this.eStatus = eStatus;
	}

	public long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	@Override
	public String toString() {
		return "EmployeeLeaveStatusDto [leaveStatusId=" + leaveStatusId + ", empId=" + empId + ", leaveDate="
				+ leaveDate + ", eStatus=" + eStatus + ", modifiedBy=" + modifiedBy + "]";
	}

}