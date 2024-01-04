package com.eidiko.portal.repo.biometric;


import com.eidiko.portal.entities.biometric.BiometricEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface BiometricRepository extends JpaRepository<BiometricEntity, Long> {

	@Query(value = "select *from employee_biometric ORDER BY BIO_DATE DESC LIMIT 1", nativeQuery = true)
	BiometricEntity findLastRecord();
	
	 public List<BiometricEntity> findByEmpIdAndBioDateBetween(@Param("empId")long empId,@Param("fromBioDate")Timestamp fromBioDate,@Param("toBioDate") Timestamp toBioDate);
	 
	 
}
