package com.eidiko.portal.service.reportingmanager;

import com.eidiko.portal.dto.reportingmanager.AlertMailDto;

import java.util.Map;

public interface AlertMailService {

    public Map<String, Object> getUnreadWarningMailsByEmpId(long empId);

    public Map<String,Object> markAsRead(AlertMailDto dto);

    public Map<String, Object> markAsUpdate(AlertMailDto dto);
}
