package com.eidiko.portal.entities.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bands {

	@Id
	@Column(name = "BAND_ID")
	private int bandId;
	
	@JsonIgnore
	@OneToMany(mappedBy = "band", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<EmployeeBandDesgMapping> bandDesgMappings = new HashSet<>();
	
}
