package com.eidiko.portal.dto.biometric;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BiometricData {
	
	@JsonProperty
	private String empId;
	@JsonProperty
	private String bioDate;

}
