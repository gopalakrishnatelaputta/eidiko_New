package com.eidiko.portal.repo.employee;

import com.eidiko.portal.entities.employee.EmployeeAccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpAccessLvlRepo extends JpaRepository<EmployeeAccessLevel, Integer>{

    EmployeeAccessLevel findByAccessLvlIdIn(List<Integer> accessLvlId);
}
