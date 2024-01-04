package com.eidiko.portal.serviceimpl.reportingmanagerimpl;

import com.eidiko.portal.dto.reportingmanager.AlertMailDto;
import com.eidiko.portal.entities.reportingmanager.AlertWarningMail;
import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.repo.reportingmanager.AlertWarningMailRepository;
import com.eidiko.portal.service.reportingmanager.AlertMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class AlertMailServiceImpl  implements AlertMailService {

@Autowired
    private AlertWarningMailRepository alertWarningMailRepository;

    Map<String,Object> map=new HashMap<>();


    public Map<String, Object> getUnreadWarningMailsByEmpId(long empId) {

        List<AlertWarningMail> list = alertWarningMailRepository.findByEmpIdAndReadByLessThan(empId, 3);
        map.put("message", ConstantValues.SUCCESS_MESSAGE);
          map.put("result", list);
           map.put("status", HttpStatus.OK.value());
           return map;
    }

    public Map<String,Object> markAsRead(AlertMailDto dto) {
        for(Long warningMailId:dto.getWarningMailId()) {
            AlertWarningMail mail = alertWarningMailRepository.getById(warningMailId);
            if (mail.getReadBy() < 3) {
                mail.setReadBy(mail.getReadBy() + 1);
                alertWarningMailRepository.save(mail);
                map.put("message", ConstantValues.SUCCESS_MESSAGE);
                map.put("status", HttpStatus.OK.value());

            }
        }
        return map;
    }


    public Map<String, Object> markAsUpdate(AlertMailDto dto) {
        for (Long warningMailId : dto.getWarningMailId()) {
            AlertWarningMail mail = alertWarningMailRepository.getById(warningMailId);
            if (mail.getReadBy() < 3) {
                mail.setReadBy(3);
                alertWarningMailRepository.save(mail);
                map.put("message", ConstantValues.SUCCESS_MESSAGE);
                map.put("status", HttpStatus.OK.value());
            }
        }
        return map;
    }
}
