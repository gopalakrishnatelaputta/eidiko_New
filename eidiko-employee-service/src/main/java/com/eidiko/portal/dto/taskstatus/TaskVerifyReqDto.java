package com.eidiko.portal.dto.taskstatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskVerifyReqDto {

	
	private List<Long> taskDetailsId = new ArrayList<>();
	
	private long verifiedById;
	
}
