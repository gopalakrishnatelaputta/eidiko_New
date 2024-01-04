package com.eidiko.portal.controller.reportingmanager;


import com.eidiko.portal.dto.reportingmanager.AlertMailDto;

import com.eidiko.portal.serviceimpl.reportingmanagerimpl.AlertMailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/alertMail")
public class AlertWarningMailController {
    @Autowired
    private AlertMailServiceImpl alertMailService;




    @GetMapping("/get-mails-by-empId-unread/{empId}")
    public Map<String,Object> getUnreadWarningMailsByEmpId(@PathVariable("empId") long empId) {
        return alertMailService.getUnreadWarningMailsByEmpId(empId);

    }

    @PutMapping("/mark-as-read")
    public Map<String,Object> markAsRead(@RequestBody AlertMailDto dto) {
       return alertMailService.markAsRead(dto);
    }


    @PutMapping("/mark-as-update")
    public Map<String, Object> markAlertMailsAsReadAsUpdate(@RequestBody AlertMailDto dto) {
        return alertMailService.markAsUpdate(dto);
    }
}
