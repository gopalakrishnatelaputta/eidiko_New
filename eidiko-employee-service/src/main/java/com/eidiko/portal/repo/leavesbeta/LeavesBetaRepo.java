package com.eidiko.portal.repo.leavesbeta;

import com.eidiko.portal.entities.leaves.interfaces.LeaveStatusCounts;
import com.eidiko.portal.entities.leavesbeta.LeavesBeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeavesBetaRepo extends JpaRepository<LeavesBeta, Long> {
    List<LeavesBeta> findByLeaveId(long leaveId);
    List<LeavesBeta> findByEmpIdAndLeaveDateBetween(long empId, LocalDate fromDate, LocalDate toDate);
    List<LeavesBeta> findAllByStatusIgnoreCase(String status);
    List<LeavesBeta> findAllByEmpIdAndStatusIgnoreCase(long empId, String status);
    List<LeavesBeta> findAllByLeaveId(long leaveId);
    List<LeavesBeta> findAllByLeaveDateBetweenAndStatusIgnoreCase(LocalDate fromDate, LocalDate toDate, String status);
    List<LeavesBeta> findByEmpIdAndLeaveDateBetweenAndStatusIgnoreCase(long empId, LocalDate fromDate, LocalDate toDate, String status);
    List<LeavesBeta> findAllByLeaveDateBetween(LocalDate fromDate, LocalDate toDate);
    @Query(value = "SELECT * " +
            "FROM leaves_beta " +
            "WHERE leave_date >= :fromDate " +
            "AND leave_date <= :toDate " +
            "AND leave_type IN ('SL', 'CL')" +
            "AND status IN ('Approved','Pending')" +
            "AND is_half_day = '0' ", nativeQuery = true)
    List<LeavesBeta> empLeavesReport(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
    @Query(value = "SELECT * " +
            "FROM leaves_beta " +
            "WHERE emp_id = :empId " +
            "AND leave_date >= :fromDate " +
            "AND leave_date <= :toDate " +
            "AND status IN ('Approved','Pending')", nativeQuery = true)
    List<LeavesBeta> findByEmpIdAndStatusAndLeaveDateBetween(
            @Param("empId") long empId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query(value = "SELECT * " +
            "FROM leaves_beta " +
            "WHERE emp_id =:empId " +
            "AND leave_date >= :fromDate " +
            "AND leave_date <= :toDate " +
            "AND leave_type IN ('SL', 'CL')" +
            "AND status IN ('Approved','Pending')" +
            "AND is_half_day = '0' " , nativeQuery = true)
    List<LeavesBeta> reportingEmpLeavesReport(
            @Param("empId")long empId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

}
