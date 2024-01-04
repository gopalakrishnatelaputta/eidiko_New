package com.eidiko.portal.entities.taskstatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EMPLOYEE")
public class EmployeeStatusReport {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EMP_ID")
	@Min(1)
	private Long empId;
	//@Column(name = "EMP_NAME")
	private String empName;
	private Date dateOfJoining;


	//@Column(name = "EMAIL_ID")
	private String emailId;
	//@Column(name = "CONTACT_NO")
	private String contactNo;
	private boolean isDeleted;

	@OneToMany(mappedBy = "employee", fetch = FetchType.EAGER)
	private List<DailyStatusReport> dailyStatusReport = new ArrayList<>();

	@OneToMany(mappedBy = "assignedBy", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<DailyStatusReport> assignedBy = new ArrayList<>();

	@OneToMany(mappedBy = "verifiedBy", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<DailyStatusReport> verifiedBy = new ArrayList<>();

	@OneToOne(mappedBy = "employee")
	private TaskStatusExemptionEmp taskStatusExemptionEmp;

	@OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private TaskStatusExemptionEmp modifiedBy;

	public Long getEmpId() {
		return empId;
	}

	public void setEmpId(Long empId) {
		this.empId = empId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public List<DailyStatusReport> getDailyStatusReport() {
		return dailyStatusReport;
	}

	public void setDailyStatusReport(List<DailyStatusReport> dailyStatusReport) {
		this.dailyStatusReport = dailyStatusReport;
	}

	public List<DailyStatusReport> getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(List<DailyStatusReport> assignedBy) {
		this.assignedBy = assignedBy;
	}

	public List<DailyStatusReport> getVerifiedBy() {
		return verifiedBy;
	}

	public void setVerifiedBy(List<DailyStatusReport> verifiedBy) {
		this.verifiedBy = verifiedBy;
	}

	public TaskStatusExemptionEmp getTaskStatusExemptionEmp() {
		return taskStatusExemptionEmp;
	}

	public void setTaskStatusExemptionEmp(TaskStatusExemptionEmp taskStatusExemptionEmp) {
		this.taskStatusExemptionEmp = taskStatusExemptionEmp;
	}

	public TaskStatusExemptionEmp getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(TaskStatusExemptionEmp modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getDateOfJoining() {
		return dateOfJoining;
	}

	public void setDateOfJoining(Date dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
	}
	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}
}
