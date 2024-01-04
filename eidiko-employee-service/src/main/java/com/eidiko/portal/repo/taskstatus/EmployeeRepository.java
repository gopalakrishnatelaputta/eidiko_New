package com.eidiko.portal.repo.taskstatus;

import com.eidiko.portal.entities.taskstatus.EmployeeStatusReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeStatusReport, Long> {
    List<EmployeeStatusReport> findByIsDeleted(boolean b);
}
