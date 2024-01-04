package com.eidiko.portal.repo.employee;

import com.eidiko.portal.entities.employee.EmpAccessLvlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpAccessLvlMappingRepo extends JpaRepository<EmpAccessLvlMapping, Long> {
    EmpAccessLvlMapping findByEmployeeEmpId(Long empId);

    int deleteByAccessLvlMappingId(long accessLvlId);

    EmpAccessLvlMapping findByEmployeeEmpIdAndAccessLevelAccessLvlId(long empId, long accessLvlId);

    List<EmpAccessLvlMapping> findByAccessLevelAccessLvlId(int accessLevel);
}
