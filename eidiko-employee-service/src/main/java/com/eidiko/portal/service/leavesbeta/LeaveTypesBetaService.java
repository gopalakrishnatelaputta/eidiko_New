package com.eidiko.portal.service.leavesbeta;

import com.eidiko.portal.entities.leavesbeta.LeaveTypesBeta;

import java.util.Map;

public interface LeaveTypesBetaService {

    public Map<String,Object> add(LeaveTypesBeta leaveTypesBeta);
    public Map<String,Object> getAll();
    public Map<String,Object> deleteLeaveType(long leaveTypeId);

}
