package com.eidiko.portal.helper.taskstatus;

import com.eidiko.portal.dto.taskstatus.DailyStatusReportResponseDto;
import com.eidiko.portal.entities.employee.ResignedEmployee;
import com.eidiko.portal.entities.taskstatus.DailyStatusReport;
import com.eidiko.portal.repo.employee.ResignedEmpRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class Loop {

	
	private static  ResignedEmpRepo resignedEmpRepo;
	
	 @Autowired
	    public Loop(ResignedEmpRepo resignedEmpRepo) {
	        this.resignedEmpRepo = resignedEmpRepo;
	    }
	
    private final static Logger logger = LoggerFactory.getLogger(Loop.class);
 
    
    private static final Map<String, Object> map = new HashMap<>();
    public static Map<String, Object> forLoop(Page<DailyStatusReport> page) {
    	List<ResignedEmployee> resgnEmpList = resignedEmpRepo.findAll();
        Set<Long> resignedEmpIds = resgnEmpList.stream()
                .map(ResignedEmployee::getEmpId)
                .collect(Collectors.toSet());

        List<DailyStatusReportResponseDto> responseList = new ArrayList<>();
        for (DailyStatusReport d : page) {
            DailyStatusReportResponseDto dailyStatusReportResponseDto = new DailyStatusReportResponseDto();
            dailyStatusReportResponseDto.setTaskDetailsId(d.getTaskDetailsId());
            dailyStatusReportResponseDto.setDesc(d.getDesc());
            dailyStatusReportResponseDto.setStatus(d.getStatus());
            dailyStatusReportResponseDto.setReason(d.getReason());
            dailyStatusReportResponseDto.setTeam(d.getTeam());
            dailyStatusReportResponseDto.setEmpId(d.getEmployee().getEmpId());
            dailyStatusReportResponseDto.setStatusReportDate(d.getStatusReportDate());
            dailyStatusReportResponseDto.setTaskDetail(d.getTaskDetails());
            if (d.getVerifiedBy() != null) {
                dailyStatusReportResponseDto.setVerifiedBy(d.getVerifiedBy().getEmpId());
                dailyStatusReportResponseDto.setVerifiedByName(d.getVerifiedBy().getEmpName());
            } else {
                dailyStatusReportResponseDto.setVerifiedBy(null);
            }
            dailyStatusReportResponseDto.setAssignedDate(d.getAssignedDate());
            dailyStatusReportResponseDto.setAssignedBy(d.getAssignedBy().getEmpId());
            dailyStatusReportResponseDto.setAssignedByName(d.getAssignedBy().getEmpName());
            if (resignedEmpIds.contains(d.getEmployee().getEmpId())) {
                continue;
            }
            responseList.add(dailyStatusReportResponseDto);
        }
        if (!page.isEmpty()) {
            map.put(ConstantValues.RESULT, responseList);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
            map.put(ConstantValues.MESSAGE, ConstantValues.SUCCESS_MESSAGE);
            map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);

        } else {
            map.put(ConstantValues.RESULT, responseList);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
            map.put(ConstantValues.MESSAGE, ConstantValues.FAILED_MESSAGE);
            map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
        }
        map.put(ConstantValues.CURRENT_PAGE, page.getNumber());
        map.put(ConstantValues.TOTAL_ITEMS, page.getTotalElements());
        map.put(ConstantValues.TOTAL_PAGES, page.getTotalPages());
        logger.info(ConstantValues.RESULT + ResponseEntity.ok(map));
        return map;

    }

    public static Map<String, Object> ifLoop(List<DailyStatusReport> list) {

        List<DailyStatusReportResponseDto> responseList = new ArrayList<>();
        List<ResignedEmployee> resgnEmpList = resignedEmpRepo.findAll();
        Set<Long> resignedEmpIds = resgnEmpList.stream()
                .map(ResignedEmployee::getEmpId)
                .collect(Collectors.toSet());
        for (DailyStatusReport d : list) {
            DailyStatusReportResponseDto dailyStatusReportResponseDto = new DailyStatusReportResponseDto();

            if (d.getVerifiedBy() == null) {
                dailyStatusReportResponseDto.setTaskDetailsId(d.getTaskDetailsId());
                dailyStatusReportResponseDto.setDesc(d.getDesc());
                dailyStatusReportResponseDto.setStatus(d.getStatus());
                dailyStatusReportResponseDto.setReason(d.getReason());
                dailyStatusReportResponseDto.setTeam(d.getTeam());
                dailyStatusReportResponseDto.setEmpId(d.getEmployee().getEmpId());
                dailyStatusReportResponseDto.setStatusReportDate(d.getStatusReportDate());
                dailyStatusReportResponseDto.setTaskDetail(d.getTaskDetails());
                dailyStatusReportResponseDto.setAssignedDate(d.getAssignedDate());
                dailyStatusReportResponseDto.setAssignedBy(d.getAssignedBy().getEmpId());
                dailyStatusReportResponseDto.setAssignedByName(d.getAssignedBy().getEmpName());
                if (resignedEmpIds.contains(d.getEmployee().getEmpId())) {
                    continue;
                }
                responseList.add(dailyStatusReportResponseDto);

            }

        }

        if (!list.isEmpty()) {
            map.put(ConstantValues.RESULT, responseList);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
            map.put(ConstantValues.MESSAGE, ConstantValues.SUCCESS_MESSAGE);
            map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);

        } else {
            map.put(ConstantValues.RESULT, responseList);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
            map.put(ConstantValues.MESSAGE, ConstantValues.FAILED_MESSAGE);
            map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
        }
        logger.info(ConstantValues.RESULT + ResponseEntity.ok(map));
        return map;

    }
}
