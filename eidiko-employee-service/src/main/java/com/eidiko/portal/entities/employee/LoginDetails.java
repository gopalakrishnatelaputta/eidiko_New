package com.eidiko.portal.entities.employee;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;



@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class LoginDetails implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long loginDetailsId;
	private String password;
	private String status;
	
	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_id")
	private Employee employee;


	public LoginDetails(long loginDetailsId, String password, String status, Employee employee) {
		super();
		this.loginDetailsId = loginDetailsId;
		this.password = password;
		this.status = status;
		this.employee = employee;
	}


	public long getLoginDetailsId() {
		return loginDetailsId;
	}


	public void setLoginDetailsId(long loginDetailsId) {
		this.loginDetailsId = loginDetailsId;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Employee getEmployee() {
		return employee;
	}


	public void setEmployee(Employee employee) {
		this.employee = employee;
	}


	public LoginDetails() {
		super();
		
	}
	
	
	
	
}
