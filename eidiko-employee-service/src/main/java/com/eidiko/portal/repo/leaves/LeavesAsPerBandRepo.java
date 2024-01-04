package com.eidiko.portal.repo.leaves;

import com.eidiko.portal.entities.leaves.LeavesAsPerBand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeavesAsPerBandRepo extends JpaRepository<LeavesAsPerBand, Long>{

	
	String DUPLICATE_LEAVE_AS_PER_BAND_ID_GET_QUERY="SELECT `leaves_as_per_band_id`\r\n"
			+ "    FROM `leaves_as_per_band`\r\n"
			+ "    WHERE `leaves_as_per_band_id` NOT IN\r\n"
			+ "    (\r\n"
			+ "SELECT MAX(`leaves_as_per_band_id`)\r\n"
			+ "        FROM `leaves_as_per_band`\r\n"
			+ "        GROUP BY emp_id, band, year\r\n"
			+ "        )";
	
	@Query(value = DUPLICATE_LEAVE_AS_PER_BAND_ID_GET_QUERY, nativeQuery = true)
	public List<Long> duplicateLeavesAsPerBandIds();
	
	public List<LeavesAsPerBand> findAllByYear(int year);
	
	public LeavesAsPerBand findByEmpIdAndYear(long empId,int year);
	
}
