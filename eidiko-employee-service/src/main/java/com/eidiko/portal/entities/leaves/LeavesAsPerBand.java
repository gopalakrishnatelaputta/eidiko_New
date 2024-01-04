package com.eidiko.portal.entities.leaves;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "leaves_as_per_band")
@ToString
public class LeavesAsPerBand {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "leaves_as_per_band_id")
	private long leavesAsPerBandId;
	@Column(name = "emp_id")
	private long empId;
	@Column(name = "band")
	private String band;
	@Column(name = "leaves")
	private int leaves;
	@Column(name = "year")
	private int year;
	
	
	
}
