package com.eidiko.portal.entities.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Designations {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int designationId;
	private String designationName;
	
	@JsonIgnore
	@OneToMany(mappedBy = "designation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<EmployeeBandDesgMapping> bandDesgMappings = new HashSet<>();
	
	
}
