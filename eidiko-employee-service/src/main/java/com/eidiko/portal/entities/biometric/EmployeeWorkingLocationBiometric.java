package com.eidiko.portal.entities.biometric;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "empWorkingLocation")
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class EmployeeWorkingLocationBiometric implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private long empWorkLocationId;
	private Date startDate;
	private Date endDate;
	private String workingFrom;
	private String location;
	private long modifiedBy;

	@Transient
	private String modifiedByWithName;

	@Column(name = "EMP_ID")
	private long empId;

}