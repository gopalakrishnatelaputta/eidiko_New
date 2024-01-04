package com.eidiko.portal.entities.biometric;

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
import java.sql.Time;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@Table(name = "EmpShiftTimings")
public class EmpShiftTimingsBiometric implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long shiftTimingId;
	private Date startDate;
	private Date endDate;
	private Time shiftStartTime;
	private Time shiftEndTime;

	private String weekOff;
	private String modifiedBy;
	
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="EMP_ID")
	private EmployeeBiometric employee;
	
	
	
}
