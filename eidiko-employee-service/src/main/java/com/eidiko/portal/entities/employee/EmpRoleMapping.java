package com.eidiko.portal.entities.employee;

import jakarta.persistence.*;

import java.io.Serializable;



@Entity
public class EmpRoleMapping implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long empRoleId;
	
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "emp_id")
	private Employee employee;
	
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id")
	private Role roles;


	public EmpRoleMapping() {
		super();
	}


	public EmpRoleMapping(long empRoleId, Employee employee, Role roles) {
		super();
		this.empRoleId = empRoleId;
		this.employee = employee;
		this.roles = roles;
	}


	public long getEmpRoleId() {
		return empRoleId;
	}


	public void setEmpRoleId(long empRoleId) {
		this.empRoleId = empRoleId;
	}


	public Employee getEmployee() {
		return employee;
	}


	public void setEmployee(Employee employee) {
		this.employee = employee;
	}


	public Role getRoles() {
		return roles;
	}


	public void setRoles(Role roles) {
		this.roles = roles;
	}
	
	
	

}
