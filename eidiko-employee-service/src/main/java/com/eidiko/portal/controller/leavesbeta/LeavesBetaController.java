package com.eidiko.portal.controller.leavesbeta;

import com.eidiko.portal.dto.leavesbeta.LeavesBetaReqDto;
import com.eidiko.portal.entities.leavesbeta.LeaveTypesBeta;
import com.eidiko.portal.helper.employee.AuthAssignedConstants;
import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.service.leavesbeta.LeaveTypesBetaService;
import com.eidiko.portal.service.leavesbeta.LeavesBetaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/leaves-beta")
@Slf4j
public class LeavesBetaController {
    @Autowired
    private LeavesBetaService leavesBetaService;
    @Autowired
    private LeaveTypesBetaService leaveTypesBetaService;

    Map<String, Object> map = new HashMap<>();
    @PostMapping("/apply-leave-by-employee")
    @PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
    public Map<String, Object> applyLeaveByEmployee(@ModelAttribute LeavesBetaReqDto betaReqDto) throws IOException {

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Check if it's past 4 PM on the 25th of the month
        if (currentDate.getDayOfMonth() == 25 && currentTime.isAfter(LocalTime.of(16, 0))) {
            // It's past the deadline, disallow leave application
            map.put(ConstantValues.STATUS_CODE, ConstantValues.ERROR_MESSAGE);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.BAD_REQUEST.value());
            map.put(ConstantValues.MESSAGE, "Leave application is not allowed after 4 PM on the 25th of the month.");
        } else {
            this.leavesBetaService.applyLeaveByEmployee(betaReqDto);
            map.put(ConstantValues.STATUS_CODE, ConstantValues.CREATED);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.CREATED.value());
            map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);
        }
        return map;
    }

    @PostMapping("/apply-leave-by-hrteam")
    @PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
    public Map<String, Object> applyLeaveByHrTeam(@ModelAttribute LeavesBetaReqDto betaReqDto) throws AuthenticationException {

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        if (currentDate.getDayOfMonth() == 25 && currentTime.isAfter(LocalTime.of(16, 0))) {
            // It's past the deadline, disallow leave application
            map.put(ConstantValues.STATUS_CODE, ConstantValues.ERROR_MESSAGE);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.BAD_REQUEST.value());
            map.put(ConstantValues.MESSAGE, "Leave application is not allowed after 4 PM on the 25th of the month.");
        } else {
            this.leavesBetaService.applyLeaveByHrteam(betaReqDto);
            map.put(ConstantValues.STATUS_CODE, ConstantValues.CREATED);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.CREATED.value());
            map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);
        }
        return map;
    }

    @PutMapping("/update-leave-of-employee/{leavesBetaId}")
    @PreAuthorize(AuthAssignedConstants.ADMIN_LEVEL_ACCESS)
    public Map<String, Object> updateAppliedLeave(@PathVariable("leavesBetaId") long leavesBetaId, @ModelAttribute LeavesBetaReqDto betaReqDto) throws AuthenticationException {
        this.leavesBetaService.updateAppliedLeave(leavesBetaId,betaReqDto);
        map.put(ConstantValues.STATUS_CODE,ConstantValues.UPDATED);
        map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
        map.put(ConstantValues.MESSAGE,ConstantValues.PROCESSED_SUCCESSFULLY);
        return map;
    }

    @GetMapping("/get-leaves-beta-by-emp")
    @PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
    public Map<String, Object> getLeavesBetaByEmpId(@RequestParam("empId") long empId, @RequestParam("year") int year) {
        return this.leavesBetaService.getLeavesByEmpId(empId,year);
    }

    @DeleteMapping("/delete-applied-leave")
    @PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
    public Map<String, Object> deleteAppliedLeave(@RequestParam("leaveBetaId") long leaveBetaId,
                                                  @RequestParam("comments") String comments) throws AuthenticationException {
        this.leavesBetaService.deleteAppliedLeave(leaveBetaId,comments);
        map.put(ConstantValues.STATUS_CODE,ConstantValues.DELETE);
        map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
        map.put(ConstantValues.MESSAGE,ConstantValues.PROCESSED_SUCCESSFULLY);
        return map;
    }

    @PreAuthorize(AuthAssignedConstants.ADMIN_LEVEL_ACCESS)
    @PostMapping("/add-leave-types")
    public Map<String,Object>add(@RequestBody LeaveTypesBeta leaveTypesBeta){
        return this.leaveTypesBetaService.add(leaveTypesBeta);
    }

    // @PreAuthorize(AuthAssignedConstants.ADMIN_LEVEL_ACCESS)
    @GetMapping("/get-all-leave-types")
    public Map<String,Object>getAll(){
        return this.leaveTypesBetaService.getAll();
    }

    @PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
    @DeleteMapping("/delete-leave-types/{leaveTypeId}")
    public Map<String,Object>deleteLeaveType(@PathVariable("leaveTypeId")long leaveTypeId){
        return this.leaveTypesBetaService.deleteLeaveType(leaveTypeId);
    }

    @GetMapping("/get-employee-pending-leaves/{leaveId}")
    public Map<String,Object>getPendingLeaves(@PathVariable("leaveId") long leaveId){
        return leavesBetaService.getPendingLeavesById(leaveId);
    }
    @PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
    @GetMapping("/get-all-pending-leaves")
    public Map<String, Object> getAllPendingLeavesToManager(){
        return this.leavesBetaService.getAllPendingLeavesToManager();
    }

    @PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
    @GetMapping("/get-all-employees-pending-leaves")
    public Map<String, Object> getAllEmployeesPendingLeaves(){
        return this.leavesBetaService.getAllEmployeesPendingLeaves();
    }

    @PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
    @PutMapping("/approve-employee-leave/{leaveId}/{status}")
    public Map<String,Object> approveLeaves(@PathVariable("leaveId") long leaveId,@PathVariable("status") String status,@RequestParam("comments") String comments){
        return leavesBetaService.approveLeaves(leaveId,status,comments);
    }

    @GetMapping("/reporting-manager/get-employees-leaves-status/{month}/{year}/{status}")
    @PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
    public Map<String, Object> getEmpLeaveReportToManager(@PathVariable("month") int month,@PathVariable("year") int year,@PathVariable("status") String status){
        return leavesBetaService.getEmpLeaveReportToManager(month,year,status);
    }

    @GetMapping("/hr-team/get-all-employees-leaves-status/{month}/{year}/{status}")
    @PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
    public Map<String, Object> getAllEmpReport(@PathVariable("month") int month,@PathVariable("year") int year,@PathVariable("status") String status){
        return leavesBetaService.getAllEmpReport(month,year,status);
    }

    @GetMapping(value = "/show-attachment")
    //@PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
    public Map<String, Object> showAttachments(@RequestParam("filename") String filename) {
        return this.leavesBetaService.showAttachments(filename);
    }
    @GetMapping("/leave-report/{year}")
    @PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
    public Map<String, Object> getLeavesReport(@PathVariable("year") int year) {
        return this.leavesBetaService.getEmpLeaveReport(year);
    }

    @GetMapping("/get-employee-yearly-leaves-report/{empId}/{year}")
    @PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
    public Map<String, Object> getAllYearlyEmpReport(@PathVariable("empId") long empId,@PathVariable("year") int year){
        return leavesBetaService.getAllYearlyEmpReport(empId,year);
    }

    @GetMapping("get-reporting-employees-leaves-report/{year}")
    @PreAuthorize(AuthAssignedConstants.MANAGER_LEVEL_ACCESS)
    public Map<String,Object> reportingEmployeesReport(@PathVariable("year") int year){
        return leavesBetaService.reportingEmployeesReport(year);
    }
}
