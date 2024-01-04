package com.eidiko.portal.repo.taskstatus;

import com.eidiko.portal.entities.taskstatus.DailyStatusReport;
import com.eidiko.portal.entities.taskstatus.EmployeeStatusReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyStatusRepository extends JpaRepository<DailyStatusReport, Long> {

    Optional<DailyStatusReport> findByStatusReportDateAndEmployee(Timestamp date, EmployeeStatusReport employee);

    Page<DailyStatusReport> findByStatusReportDateBetween(Timestamp fromStatusReportDate, Timestamp toStatusReportDate, Pageable pageable);

   // @Query("SELECT d FROM DailyStatusReport d WHERE d.employee = :employee AND d.statusReportDate BETWEEN :fromStatusReportDate AND :toStatusReportDate")
    Page<DailyStatusReport> findByEmployeeAndStatusReportDateBetween(EmployeeStatusReport employee, Timestamp fromStatusReportDate, Timestamp toStatusReportDate, Pageable pageable);

    Page<DailyStatusReport> findByEmployee(EmployeeStatusReport employee, Pageable pageable);

    Page<DailyStatusReport> findByStatusReportDateBetweenAndStatus(Timestamp fromStatusReportDate, Timestamp toStatusReportDate, String status, Pageable pageable);
    Page<DailyStatusReport> findByStatus(String status, Pageable pageable);
    List<DailyStatusReport> findByStatusReportDateBetween(Timestamp fromDate,Timestamp toDate);

    Page<DailyStatusReport> findAllByEmployeeEmpId(long empId,Pageable pageable);
    List<DailyStatusReport> findByEmployee(EmployeeStatusReport employee);

}

