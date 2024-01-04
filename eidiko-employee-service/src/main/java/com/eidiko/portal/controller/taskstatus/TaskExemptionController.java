package com.eidiko.portal.controller.taskstatus;

import com.eidiko.portal.dto.taskstatus.DailyStatusReportResponseDto;
import com.eidiko.portal.dto.taskstatus.TaskExemptionReqDto;
import com.eidiko.portal.exception.taskstatus.NotNullException;
import com.eidiko.portal.helper.taskstatus.ConstantValues;
import com.eidiko.portal.service.taskstatus.TaskExemptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/V1/dailyStatusReport/eligibility-check")
public class TaskExemptionController {

    @Autowired
    private TaskExemptionService taskExemptionService;

    Logger logger = LoggerFactory.getLogger(getClass());

    Map<String, Object> map = new HashMap<>();

    @PostMapping("/insert-data")
    @Operation(summary = "To Insert TaskExemption Details" ,
            description = "To Insert TaskExemption Details",tags = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Data Fetched Successfully",
                    content = {@Content(mediaType = "application/json",schema = @Schema(implementation = DailyStatusReportResponseDto.class))})})

    public ResponseEntity<Map<String, Object>> save(@RequestBody TaskExemptionReqDto taskExemptionReqDto) {

        this.taskExemptionService.save(taskExemptionReqDto);


        map.put(ConstantValues.MESSAGE , ConstantValues.CREATED);
        map.put(ConstantValues.STATUS_TEXT , HttpStatus.CREATED.value());
        map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);

        logger.info(ConstantValues.RESULT + ResponseEntity.ok(map));

        return ResponseEntity.ok(map);
    }

    @GetMapping("/get-data-by-empId/{empId}")
    @Operation(summary = "Get taskExemption details by EmployeeId" ,
            description = "To Get taskExemption details by EmployeeId",tags = "Get")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Data Fetched Successfully",
                    content = {@Content(mediaType = "application/json",schema = @Schema(implementation = DailyStatusReportResponseDto.class))})})

    public ResponseEntity<Map<String,Object>> getById(@PathVariable("empId") long empId) {
         return this.taskExemptionService.getById(empId);
    }

    @PutMapping("/update-end-date")
    @Operation(summary = "Update endDate for selected EmpId's" ,
            description = "Update endDate for selected EmpId's",tags = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Data Fetched Successfully",
                    content = {@Content(mediaType = "application/json",schema = @Schema(implementation = DailyStatusReportResponseDto.class))})})

    public  ResponseEntity<Map<String,Object>> updateEndDate(@RequestBody TaskExemptionReqDto taskExemptionReqDto) throws NotNullException {

        if(taskExemptionReqDto.getEmpId().isEmpty()) {
            throw new NotNullException(ConstantValues.ELIGIBILITY_CHECK);
        }

        this.taskExemptionService.updateEndDate(taskExemptionReqDto.getEndDate(),taskExemptionReqDto.getEmpId());

        map.put(ConstantValues.MESSAGE , ConstantValues.UPDATED);
        map.put(ConstantValues.STATUS_TEXT , HttpStatus.CREATED.value());
        map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);

        logger.info(ConstantValues.RESULT + ResponseEntity.ok(map));

        return ResponseEntity.ok(map);
    }
}
