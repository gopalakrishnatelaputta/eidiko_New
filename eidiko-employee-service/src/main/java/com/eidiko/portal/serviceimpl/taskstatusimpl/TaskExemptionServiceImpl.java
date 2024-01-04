package com.eidiko.portal.serviceimpl.taskstatusimpl;

import com.eidiko.portal.dto.taskstatus.TaskExemptionReqDto;
import com.eidiko.portal.entities.taskstatus.EmployeeStatusReport;
import com.eidiko.portal.entities.taskstatus.TaskStatusExemptionEmp;
import com.eidiko.portal.exception.taskstatus.UserNotFoundException;
import com.eidiko.portal.helper.taskstatus.ConstantValues;
import com.eidiko.portal.repo.taskstatus.EmployeeRepository;
import com.eidiko.portal.repo.taskstatus.TaskStatusExemptionRepository;
import com.eidiko.portal.service.taskstatus.TaskExemptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskExemptionServiceImpl implements TaskExemptionService {

    @Autowired
    private TaskStatusExemptionRepository statusExemptionRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void save(TaskExemptionReqDto taskExemptionReqDto) {
        List<Long> id = taskExemptionReqDto.getEmpId();
        id.forEach(empId ->
        {
            TaskStatusExemptionEmp taskStatusExemptionEmp = new TaskStatusExemptionEmp();

            EmployeeStatusReport emp = employeeRepository.findById(empId).orElseThrow(() -> new UserNotFoundException(ConstantValues.USER_NOT_FOUND));

            taskStatusExemptionEmp.setEmployee(emp);
            EmployeeStatusReport modifiedBy = employeeRepository.findById(taskExemptionReqDto.getModifiedBy()).orElseThrow(() -> new UserNotFoundException(ConstantValues.USER_NOT_FOUND + ConstantValues.WITH_GIVEN_ID + taskExemptionReqDto.getModifiedBy()));

           // logger.error(ConstantValues.RESULT + new UserNotFoundException());

            taskStatusExemptionEmp.setModifiedBy(modifiedBy);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            taskStatusExemptionEmp.setModifiedOn(timestamp);
            taskStatusExemptionEmp.setStartDate(taskExemptionReqDto.getStartDate());
            taskStatusExemptionEmp.setEndDate(taskExemptionReqDto.getEndDate());
            this.statusExemptionRepository.save(taskStatusExemptionEmp);

            //logger.info(ConstantValues.RESULT + taskStatusExemptionEmp);

        });
    }

    public ResponseEntity<Map<String, Object>> getById(long empId) {

        TaskStatusExemptionEmp taskStatusExemptionEmp = statusExemptionRepository.findByEmployeeEmpId(empId);
        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        LocalDate currentDate = timeStamp.toLocalDateTime().toLocalDate();

        Map<String, Object> map = new HashMap<>();

        if (taskStatusExemptionEmp != null && ((taskStatusExemptionEmp.getEndDate()  == null  || taskStatusExemptionEmp.getEndDate().isEqual(currentDate)) || taskStatusExemptionEmp.getEndDate().isAfter(currentDate))) {

            map.put(ConstantValues.STATUS_REPORT, ConstantValues.ELIGIBLE);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
            map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);

        } else {
            map.put(ConstantValues.STATUS_REPORT, ConstantValues.NOT_ELIGIBLE_);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
            map.put(ConstantValues.STATUS_CODE, ConstantValues.STATUS_MESSAGE);
        }

        logger.info(ConstantValues.RESULT + ResponseEntity.ok(map));
        return ResponseEntity.ok(map);

    }

    public void updateEndDate(LocalDate endDate,List<Long>empId) {

       List<TaskStatusExemptionEmp> taskStatusExemptionEmpList = statusExemptionRepository.findAllByEmployeeEmpIdIn(empId);
        taskStatusExemptionEmpList.forEach(
                taskStatusExemptionEmp -> {

                    taskStatusExemptionEmp.setEndDate(endDate);
                    statusExemptionRepository.save(taskStatusExemptionEmp);
                }
        );
    }
}



