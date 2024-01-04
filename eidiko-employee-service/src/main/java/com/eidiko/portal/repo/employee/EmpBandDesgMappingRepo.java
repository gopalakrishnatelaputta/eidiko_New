package com.eidiko.portal.repo.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eidiko.portal.entities.employee.EmployeeBandDesgMapping;

public interface EmpBandDesgMappingRepo extends JpaRepository<EmployeeBandDesgMapping, Long> {
    EmployeeBandDesgMapping findByEmployeeEmpId(long empId);

}

