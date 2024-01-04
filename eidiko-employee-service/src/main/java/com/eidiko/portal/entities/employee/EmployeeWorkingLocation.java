package com.eidiko.portal.entities.employee;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class EmployeeWorkingLocation implements Serializable{



	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long empWorkLocationId;
	private Date startDate;
	private Date endDate;
	private String workingFrom;
	private String location;
	private long modifiedBy;
	
	@Transient
	private String modifiedByWithName;
	
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EMP_ID")
	private Employee employee;
	
	
}
