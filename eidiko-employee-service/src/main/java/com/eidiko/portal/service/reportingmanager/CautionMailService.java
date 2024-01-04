package com.eidiko.portal.service.reportingmanager;

import com.eidiko.portal.dto.employee.CautionDto;
import com.eidiko.portal.entities.reportingmanager.CautionMail;
import com.eidiko.portal.exception.reportingmanager.DataNotFoundException;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Map;

public interface CautionMailService

{



    public Map<String,Object> getDescriptionsByEmpId(long empId,Pageable pageable) throws DataNotFoundException;

    public Map<String,Object> getDataByMonthAndYear(int year, int month, Pageable pageable);


    public Map<String,Object> findBySentDate(int year,Pageable pageable);

    
    public Map<String, Object> getCautionReportByDates(LocalDate fromDate,LocalDate toDate);
    
    public Map<String, Object> getCautionReportByDatesAndEmpId(LocalDate fromDate,LocalDate toDate,long empId);

    Map<String, Object> cautionMail(long empId1, CautionDto cautionDto) throws Exception;
    public Map<String, Object> cautionMailWithAttchments(long empId1, CautionDto cautionDto) throws Exception;

}
