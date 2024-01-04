package com.eidiko.portal.service.taskstatus;

import com.eidiko.portal.dto.taskstatus.TaskExemptionReqDto;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TaskExemptionService {

    void save(TaskExemptionReqDto taskExemptionReqDto);
    ResponseEntity<Map<String,Object>> getById(long empId);
    void updateEndDate(LocalDate endDate, List<Long>empId);
}
