package com.eidiko.portal.service.biometric;

import java.sql.Timestamp;
import java.util.Map;
//import com.eidiko.biometric.entity.BiometricShiftTimingsEntity;

public interface BiometricServiceGetBioDates {

	public Map<String, Object> getBiometricDataByEmpId(Timestamp fromDate, Timestamp toDate, long empId);
	
	//public List<BiometricShiftTimingsEntity> getShiftTimings(long empId);
	

}
