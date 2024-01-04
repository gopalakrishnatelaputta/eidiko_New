package com.eidiko.portal.service.leavesbeta;

import com.eidiko.portal.dto.leavesbeta.LeavesBetaReqDto;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.Map;


public interface LeavesBetaService {
    void applyLeaveByEmployee(LeavesBetaReqDto betaReqDto) throws IOException;
    void applyLeaveByHrteam(LeavesBetaReqDto betaReqDto) throws AuthenticationException;
    void updateAppliedLeave(long leavesBetaId, LeavesBetaReqDto betaReqDto) throws AuthenticationException;
    Map<String, Object> getLeavesByEmpId(long empId,int year);
    void deleteAppliedLeave(long leaveBetaId, String comments) throws AuthenticationException;
    Map<String,Object> getAllPendingLeavesToManager() ;
    Map<String,Object> getAllEmployeesPendingLeaves();
    Map<String,Object> approveLeaves(long leaveId,String status, String comments);
    Map<String,Object> getEmpLeaveReportToManager(int month, int year, String status);
    Map<String,Object> getAllEmpReport(int month, int year, String status);
    Map<String,Object>getPendingLeavesById(long leaveApplyId);
    Map<String, Object> showAttachments(String filename);
    Map<String, Object> getEmpLeaveReport(int year);
    Map<String, Object> getAllYearlyEmpReport(long empId, int year);
    Map<String,Object> reportingEmployeesReport(int year);

}
