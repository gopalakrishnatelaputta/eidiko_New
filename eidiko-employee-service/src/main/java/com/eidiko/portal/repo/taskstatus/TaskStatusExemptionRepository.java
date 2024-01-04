package com.eidiko.portal.repo.taskstatus;

import com.eidiko.portal.entities.taskstatus.TaskStatusExemptionEmp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskStatusExemptionRepository extends JpaRepository<TaskStatusExemptionEmp, Long> {

    TaskStatusExemptionEmp findByEmployeeEmpId(long empId);
   List<TaskStatusExemptionEmp> findAllByEmployeeEmpIdIn(List<Long> empId);


}
