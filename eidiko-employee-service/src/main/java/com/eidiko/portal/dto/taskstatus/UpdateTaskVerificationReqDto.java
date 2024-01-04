package com.eidiko.portal.dto.taskstatus;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateTaskVerificationReqDto {
	
	private List <@NotNull(message = "Please select any task to verify ") Long> taskDetailsId = new ArrayList<>(); 
	
	private long verifiedById;
	
	
}
