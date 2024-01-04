package com.eidiko.portal.entities.employee;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ClientLocations {

	@Id
	@GeneratedValue
	private int clientLocationId;
	
	private String location;
	private String desc;
	
	
	
}
