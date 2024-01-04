package com.eidiko.portal.entities.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name="employee")
@ToString
public class Employee implements UserDetails {
	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}
	


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "EMP_ID")
	private Long empId;
	//@Column(name = "EMP_NAME")
	private String empName;
	//@Column(name = "EMAIL_ID")
	private String emailId;
	//@Column(name = "DATE_OF_JOINING")
	private Date dateOfJoining;
	//@Column(name = "MODIFIED_DATE")
	private Timestamp modifiedDate;
	//@Column(name = "CONTACT_NO")
	private String contactNo;
	//@Column(name = "CREATED_BY")
	private long createdBy;
	//@Column(name = "IS_DELETED")
	private boolean isDeleted;
	//@Column(name = "STATUS")
	private String status;
	//@Column(name = "ABOUT")
	private String about;
	private LocalDate dateOfBirth;

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	@Transient
	private String designation;
	



	@JsonIgnore
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<EmpRoleMapping> empRoles = new HashSet<>();

	@JsonIgnore
	@OneToOne(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private LoginDetails loginDetails;

	@JsonIgnore
	@OneToMany(mappedBy = "employee", fetch = FetchType.EAGER)
	private Set<EmpAccessLvlMapping> accessLvl = new HashSet<>();


	@JsonIgnore
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ReportingManager> reportingManagerForEmployee = new HashSet<>();


	@JsonIgnore
	@OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ReportingManager> reportingManagerForManager = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<EmployeeWorkingLocation> employeeWorkingLocations = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<EmpShiftTimings> empShiftTimings = new HashSet<>();

	@JsonIgnore
	@OneToOne(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.ALL )
	private EmpProfilePic empProfilePic;
	
	
	@JsonIgnore
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<EmployeeBandDesgMapping> bandDesgMappings = new HashSet<>();
	
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.accessLvl.stream().map(role -> new SimpleGrantedAuthority(role.getAccessLevel().getAccessLvlName()))
				.collect(Collectors.toList());
	}

	@Override
	public String getUsername() {

		return String.valueOf(empId);
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return isDeleted;
	}

	public Employee() {
		super();

	}
	

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



	public Date getDateOfJoining() {
		return dateOfJoining;
	}

	public void setDateOfJoining(Date dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
	}

	public Timestamp getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Set<EmpRoleMapping> getEmpRoles() {
		return empRoles;
	}

	public void setEmpRoles(Set<EmpRoleMapping> empRoles) {
		this.empRoles = empRoles;
	}

	public LoginDetails getLoginDetails() {
		return loginDetails;
	}

	public void setLoginDetails(LoginDetails loginDetails) {
		this.loginDetails = loginDetails;
	}

	@Override
	@JsonIgnore
	public String getPassword() {
		return loginDetails.getPassword();
	}

	public Set<EmpAccessLvlMapping> getAccessLvl() {
		return accessLvl;
	}

	public void setAccessLvl(Set<EmpAccessLvlMapping> accessLvl) {
		this.accessLvl = accessLvl;
	}

	public boolean isDeleted() {
		return isDeleted;
	}


	public Set<ReportingManager> getReportingManagerForEmployee() {
		return reportingManagerForEmployee;
	}

	public void setReportingManagerForEmployee(Set<ReportingManager> reportingManagerForEmployee) {
		this.reportingManagerForEmployee = reportingManagerForEmployee;
	}


	public Set<ReportingManager> getReportingManagerForManager() {
		return reportingManagerForManager;
	}

	public void setReportingManagerForManager(Set<ReportingManager> reportingManagerForManager) {
		this.reportingManagerForManager = reportingManagerForManager;
	}

	public Set<EmployeeWorkingLocation> getEmployeeWorkingLocations() {
		return employeeWorkingLocations;
	}

	public void setEmployeeWorkingLocations(Set<EmployeeWorkingLocation> employeeWorkingLocations) {
		this.employeeWorkingLocations = employeeWorkingLocations;
	}





	public Set<EmpShiftTimings> getEmpShiftTimings() {
		return empShiftTimings;
	}

	public void setEmpShiftTimings(Set<EmpShiftTimings> empShiftTimings) {
		this.empShiftTimings = empShiftTimings;
	}


	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}



	public EmpProfilePic getEmpProfilePic() {
		return empProfilePic;
	}

	public void setEmpProfilePic(EmpProfilePic empProfilePic) {
		this.empProfilePic = empProfilePic;
	}
	

	public Set<EmployeeBandDesgMapping> getBandDesgMappings() {
		return bandDesgMappings;
	}

	public void setBandDesgMappings(Set<EmployeeBandDesgMapping> bandDesgMappings) {
		this.bandDesgMappings = bandDesgMappings;
	}

	@Override
	public String toString() {
		return "Employee [empId=" + empId + ", empName=" + empName + ", emailId=" + emailId + ", dateOfJoining="
				+ dateOfJoining + ", modifiedDate=" + modifiedDate + ", contactNo=" + contactNo + ", createdBy="
				+ createdBy + ", isDeleted=" + isDeleted + ", status=" + status + ", empRoles=" + empRoles
				+ ", loginDetails=" + loginDetails + "]";
	}

}
