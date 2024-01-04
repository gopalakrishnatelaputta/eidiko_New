package com.eidiko.portal.serviceimpl.biometricimpl;

import com.eidiko.portal.entities.biometric.BiometricEntity;
import com.eidiko.portal.helper.biometric.ConstantValues;
import com.eidiko.portal.repo.biometric.BiometricRepository;
import com.eidiko.portal.service.biometric.BiometricServiceGetBioDates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BiometricServiceGetBioDatesImpl implements BiometricServiceGetBioDates {

	@Autowired
	private BiometricRepository biometricRepository;

	@Override
	public Map<String, Object> getBiometricDataByEmpId(Timestamp fromDate, Timestamp toDate, long empId) {

		List<BiometricEntity> listOfDates = biometricRepository.findByEmpIdAndBioDateBetween(empId, fromDate, toDate);
		Map<String, Object> map = new HashMap<>();
		if (!listOfDates.isEmpty()) {

			map.put(ConstantValues.RESULT, listOfDates);
			map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
		} else {
			map.put(ConstantValues.MESSAGE, ConstantValues.NO_DATA_FETCHED_SUCCESS_TEXT);
			map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
			map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
			map.put(ConstantValues.RESULT, new ArrayList<>());

		}
		return map;
	}
}
