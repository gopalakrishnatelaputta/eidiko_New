package com.eidiko.portal.entities.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeBandDesgMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long empBandDesgMapId;
	
	private Date effectiveDate;
	private Timestamp modifiedDate;
	private long modifiedBy;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "EMP_ID")
	private Employee employee;
	
	
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "band_id")
	private Bands band;
	

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "desg_id")
	private Designations designation;
	
	
	
	
}
