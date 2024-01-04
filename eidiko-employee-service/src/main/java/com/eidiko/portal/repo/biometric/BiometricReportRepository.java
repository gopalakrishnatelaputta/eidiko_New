package com.eidiko.portal.repo.biometric;


import com.eidiko.portal.entities.biometric.BiometricReportEntity;
import com.eidiko.portal.helper.biometric.BiometricReportProjection;
import com.eidiko.portal.helper.biometric.BiometricReportViewProjection;
import com.eidiko.portal.helper.biometric.DuplicateIdsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BiometricReportRepository extends JpaRepository<BiometricReportEntity, Long> {

	List<BiometricReportEntity> findByempId(long empId);

	BiometricReportEntity findByEmpIdAndBiometricDateAndMonthAndYear(long empid, Date date, String month, int year);

	List<BiometricReportEntity> getByempId(Object empId);

	Page<BiometricReportEntity> findByBiometricDate(Date date, Pageable pageable);

	public Page<BiometricReportEntity> findByEmpIdAndBiometricDateBetween(@Param("empId") long empId,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, Pageable pageable);

	Page<BiometricReportEntity> findByMonthAndYear(String month, Integer year, Pageable pageable);

	@Query(value = "SELECT COUNT(*) as count , r.emp_id , e.emp_name FROM emp_biometric_report r,employee e WHERE  total_working_time > 540 AND r.biometric_date BETWEEN ?1 AND ?2 AND  r.emp_id = e.emp_id GROUP BY r.emp_id ORDER BY COUNT(*) DESC ", nativeQuery = true)
	Page<BiometricReportProjection> findBytotalworkingtime(String fromDate, String toDate, Integer totalworkingtime,
														   Pageable pageable);

	@Query(value = "SELECT COUNT(*) as count, r.emp_id , e.emp_name FROM emp_biometric_report r,employee e WHERE  is_Late = 0 AND r.biometric_date BETWEEN ?1 AND ?2 AND  r.emp_id = e.emp_id GROUP BY r.emp_id ORDER BY COUNT(*) DESC", nativeQuery = true)
	Page<BiometricReportProjection> findByisLatefalse(String fromDate, String toDate, Pageable pageable);

	@Query(value = "SELECT COUNT(*) as count, r.emp_id , e.emp_name FROM emp_biometric_report r,employee e WHERE  is_Late = 1 AND r.biometric_date BETWEEN ?1 AND ?2 AND  r.emp_id = e.emp_id GROUP BY r.emp_id ORDER BY COUNT(*) DESC", nativeQuery = true)
	Page<BiometricReportProjection> findByisLatetrue(String fromDate, String toDate, Pageable pageable);

	@Query(value = "SELECT COUNT(*) as count , r.emp_id , e.emp_name FROM emp_biometric_report r,employee e WHERE  total_working_time < 540 AND r.biometric_date BETWEEN ?1 AND ?2 AND  r.emp_id = e.emp_id GROUP BY r.emp_id ORDER BY COUNT(*) DESC ", nativeQuery = true)
	Page<BiometricReportProjection> findBytotalworkingtimeLess9hrs(String fromDate, String toDate,
			Integer totalworkingtime, Pageable pageable);

	@Query(value = "SELECT COUNT(*) as count, r.emp_id , e.emp_name FROM emp_biometric_report r,employee e WHERE  is_very_Late = 1 AND r.biometric_date BETWEEN ?1 AND ?2 AND  r.emp_id = e.emp_id GROUP BY r.emp_id ORDER BY COUNT(*) DESC", nativeQuery = true)
	Page<BiometricReportProjection> findByisVeryLatetrue(String fromDate, String toDate, Pageable pageable);
	
	@Query(value = "SELECT BIOMETRIC_REPORT_ID\r\n"
			+ "    FROM emp_biometric_report\r\n"
			+ "    WHERE BIOMETRIC_REPORT_ID NOT IN\r\n"
			+ "    (\r\n"
			+ "SELECT MAX(BIOMETRIC_REPORT_ID)\r\n"
			+ "        FROM emp_biometric_report\r\n"
			+ "        GROUP BY emp_id, BIOMETRIC_DATE\r\n"
			+ "        )", nativeQuery = true)
	List<DuplicateIdsProjection> findDuplicatesInBiometricReport();
	
	
	@Query(value = "SELECT  bio.EMP_ID,\r\n"
			+ "    SUM(TOTAL_WORKING_TIME) / COUNT(*) AS avg_working_hours,\r\n"
			+ "    SUM(CASE WHEN is_late > 0 THEN 1 ELSE 0 END) AS is_late_count,\r\n"
			+ "    SUM(CASE WHEN is_very_late > 0 THEN 1 ELSE 0 END) AS very_late_count,\r\n"
			+ "    SUM(CASE WHEN (is_very_late = 0 AND is_late = 0) THEN 1 ELSE 0 END) AS no_late_count, bio.month,bio.year FROM emp_biometric_report bio WHERE emp_id = :empId  AND YEAR= :year GROUP BY bio.MONTH, bio.YEAR;\r\n"
			+ "", nativeQuery = true)
	List<BiometricReportViewProjection> findByEmpIdAndYear(@Param("empId")long empId, @Param("year") Integer year);
	
	
	boolean existsByEmpIdAndBiometricDate(long empId, LocalDate date);
	
	@Query(value = "SELECT MAX(e.BIOMETRIC_DATE) FROM emp_biometric_report e where e.MONTH = ? AND DAY(BIOMETRIC_DATE) <= 25",nativeQuery = true)
	LocalDate getHighestBiometricDate(int month);

}
