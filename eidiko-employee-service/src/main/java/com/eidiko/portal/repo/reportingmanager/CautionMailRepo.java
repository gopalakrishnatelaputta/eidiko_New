package com.eidiko.portal.repo.reportingmanager;

import com.eidiko.portal.entities.reportingmanager.CautionMail;
import com.eidiko.portal.entities.reportingmanager.interfaces.CautionMailProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CautionMailRepo extends JpaRepository<CautionMail,Long> {

    CautionMail findByEmpId(long empId);

    Page<CautionMail> findAllByEmpId(long empId,Pageable pageable);


    Page<CautionMail> findBySentDateBetween(LocalDateTime startOfMonth, LocalDateTime endOfMonth,Pageable pageable);

    @Query(value = "SELECT * FROM emp_warning_mails e WHERE YEAR(e.sent_date) = :year", nativeQuery = true)
    Page<CautionMail> findByYear(@Param("year") int year, Pageable pageable);

    public Map<String,Object> findById(long id);
    
    @Query(value = "SELECT e.EMP_NAME, m.emp_id, COUNT(*) AS total_warnings_received\r\n"
    		+ "FROM emp_warning_mails m\r\n"
    		+ "JOIN employee e ON e.EMP_ID = m.emp_id\r\n"
    		+ "WHERE m.sent_date BETWEEN ?1 AND ?2\r\n"
    		+ "GROUP BY m.emp_id;\r\n"
    		+ "",nativeQuery = true)
   List<CautionMailProjection> findCautionReportsBetweenDates(LocalDateTime fromDateTime, LocalDateTime toDateTime);
    
    @Query(value = "SELECT e.EMP_NAME, m.emp_id, COUNT(*) AS total_warnings_received\r\n"
    		+ "FROM emp_warning_mails m\r\n"
    		+ "JOIN employee e ON e.EMP_ID = m.emp_id\r\n"
    		+ "WHERE m.sent_date BETWEEN ?1 AND ?2 and m.emp_id = ?3\r\n"
    		+ "GROUP BY m.emp_id;\r\n"
    		+ "",nativeQuery = true)
   CautionMailProjection findCautionReportsBetweenDatesByEmpId(LocalDateTime fromDateTime,LocalDateTime toDateTime,long empId);
}