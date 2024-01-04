package com.eidiko.portal.serviceimpl.leavesbetaimpl;

import com.eidiko.portal.config.employee.SecurityUtil;
import com.eidiko.portal.entities.leavesbeta.LeaveTypesBeta;
import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.repo.leavesbeta.LeaveTypesBetaRepo;
import com.eidiko.portal.service.leavesbeta.LeaveTypesBetaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LeaveTypesBetaServiceImpl implements LeaveTypesBetaService {
    @Autowired
    private LeaveTypesBetaRepo typesBetaRepo;
    Map<String, Object> map = new HashMap<>();

    @Override
    public Map<String, Object> add(LeaveTypesBeta leavetypes) {
        try{
            Long empId = SecurityUtil.getCurrentUserDetails().getEmpId();
            leavetypes.setCreatedBy(empId);
            typesBetaRepo.save(leavetypes);
            map.put(ConstantValues.MESSAGE,ConstantValues.PROCESSED_SUCCESSFULLY);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
            map.put(ConstantValues.STATUS_CODE,ConstantValues.SUCCESS_MESSAGE);

        }
        catch(Exception e){
            throw new ResourceNotProcessedException(ConstantValues.NOT_PROCESSED);
        }
        return map;
    }

    @Override
    public Map<String, Object> getAll() {
        try{
            List<LeaveTypesBeta> list=this.typesBetaRepo.findAll();
            log.info("List of Leave Types : {} ",list);
            if(!list.isEmpty()) {
                map.put(ConstantValues.RESULT, list);
                map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
                map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
                map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
            }
            else{
                map.put(ConstantValues.RESULT, list);
                map.put(ConstantValues.MESSAGE, ConstantValues.NO_DATA_FETCHED_SUCCESS_TEXT);
                map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
                map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
            }
        }
        catch(Exception e){
            log.error("Exception occurred : {} ",e.getMessage());
            throw new ResourceNotProcessedException(ConstantValues.NOT_PROCESSED);
        }
        return map;
    }

    @Override
    public Map<String,Object> deleteLeaveType(long leaveTypeId){
        try{
            typesBetaRepo.deleteById(leaveTypeId);
            log.info("Leave type Deleted with id : {} ",leaveTypeId);
            map.put(ConstantValues.MESSAGE,ConstantValues.PROCESSED_SUCCESSFULLY);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
            map.put(ConstantValues.STATUS_CODE,ConstantValues.SUCCESS_MESSAGE);
        }
        catch(Exception e){
            log.error("Exception occurred : {} ",e.getMessage());
            throw new ResourceNotProcessedException(ConstantValues.NOT_DELETED);
        }
        return map;
    }
}
