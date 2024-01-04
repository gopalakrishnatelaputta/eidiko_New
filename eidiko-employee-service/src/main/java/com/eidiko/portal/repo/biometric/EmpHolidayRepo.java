package com.eidiko.portal.repo.biometric;

import com.eidiko.portal.entities.biometric.HolidayList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
public interface EmpHolidayRepo extends JpaRepository<HolidayList, Integer>{

	List<HolidayList> findAllByHolidayDate(LocalDate date);
	boolean existsByHolidayDate(LocalDate date);
	
}
