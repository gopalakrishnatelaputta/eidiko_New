package com.eidiko.portal.repo.employee;

import com.eidiko.portal.entities.employee.EmpShiftTimings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpShiftTimingRepo extends JpaRepository<EmpShiftTimings, Long> {

}
