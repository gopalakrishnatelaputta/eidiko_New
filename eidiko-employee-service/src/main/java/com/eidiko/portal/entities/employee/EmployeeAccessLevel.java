package com.eidiko.portal.entities.employee;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "ACCESS_LVL")
public class EmployeeAccessLevel implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int accessLvlId;
	private String accessLvlName;
	private String accessLvlDesc;
	
	
	@OneToMany(mappedBy = "accessLevel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<EmpAccessLvlMapping> accessLvl;
	
	
	
	
	
}
