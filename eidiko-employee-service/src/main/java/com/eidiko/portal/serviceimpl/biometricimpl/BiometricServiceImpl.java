package com.eidiko.portal.serviceimpl.biometricimpl;

import com.eidiko.portal.entities.biometric.BiometricEntity;
import com.eidiko.portal.exception.biometric.FileDataAlreadyExistsException;
import com.eidiko.portal.helper.biometric.ConstantValues;
import com.eidiko.portal.repo.biometric.BiometricRepository;
import com.eidiko.portal.service.biometric.BiometricService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class BiometricServiceImpl implements BiometricService {
	@Autowired
	private BiometricRepository biorepository;

	BiometricEntity biometricEntity = new BiometricEntity();

	@Override
	public Map<String, Object> uploadFile(MultipartFile file) {

		BufferedReader reader;

		List<BiometricEntity> list = new ArrayList<>();

		try {
			reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				String aaa = line.trim();
				log.info("{}",aaa);

				Map<String, String> separateEmpIdAndDateFromString = separateEmpIdAndDateFromString(aaa);
				BiometricEntity generateBiometricEntity = generateBiometricEntity(
						separateEmpIdAndDateFromString.get("empId"), separateEmpIdAndDateFromString.get("dateTime"));
				log.info("{}",generateBiometricEntity);
				list.add(generateBiometricEntity);
				log.info("-------------------------------------------------------------------");
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


		log.info("{}",list.size());
		if (getLastRecord() == null) {
			this.biorepository.saveAll(list);
			Map<String, Object> response = new HashMap<>();
	        response.put("status", ConstantValues.STATUS);
	        response.put("message", ConstantValues.FILE_MESSAGE);
	        response.put("statuscode", ConstantValues.STATUS_CODE);
	        return response;
		} else {
			List<BiometricEntity> removeDup = removeDup(getLastRecord(), list);
			log.info("removeDup removeDup" + " " + removeDup);
			this.biorepository.saveAll(removeDup);
			 Map<String, Object> response = new HashMap<>();
		        response.put("status", ConstantValues.STATUS);
		        response.put("message", ConstantValues.FILE_MESSAGE);
		        response.put("statuscode", ConstantValues.STATUS_CODE);
		        return response;
		}
		
	}

	private Map<String, String> separateEmpIdAndDateFromString(String line) {
		String[] split = line.split("\t");
		String empId = split[0].trim();
		String dateTime = split[1].trim();
		Map<String, String> map = new HashMap<>();
		map.put("empId", empId);
		map.put("dateTime", dateTime);

		return map;
	}

	private BiometricEntity generateBiometricEntity(String empId, String dateTime) {

		log.info(empId + "<<<>>>>" + dateTime);

		BiometricEntity biometricEntity = new BiometricEntity();
		biometricEntity.setEmpId(Long.parseLong(empId));
		Timestamp timestamp = null;
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			// java.util.Date date = dateFormat.parse(dateString);
			java.util.Date parseDate = sdf.parse(dateTime);
			log.info("parseDate" + parseDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(parseDate);
			cal.set(Calendar.MILLISECOND, 0);
			timestamp = new Timestamp(parseDate.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		biometricEntity.setBioDate(timestamp);
		return biometricEntity;
	}

	public StringBuilder getLastRecord() {
		BiometricEntity lastrec = biorepository.findLastRecord();
		if (lastrec == null) {
			return null;
		}
		log.info("lastrec" + " " + lastrec);
		StringBuilder lastempBiodatefromDB = new StringBuilder();
		String s1 = lastrec.getEmpId().toString();
		String s2 = lastrec.getBioDate().toString();
		lastempBiodatefromDB.append(s1).append(" ");
		lastempBiodatefromDB.append(s2);
		log.info("{}",lastempBiodatefromDB);
		return lastempBiodatefromDB;
	}

	private List<BiometricEntity> removeDup(StringBuilder lastempBiodatefromDB, List<BiometricEntity> list) {
		log.info("Inside removeDup");
		int count = 0;
		int temp = 0;
		List<BiometricEntity> filteredlist = new ArrayList<>();
		for (BiometricEntity obj : list) {
			count++;
			StringBuilder str = new StringBuilder();
			String s3 = obj.getEmpId().toString();
			String s4 = obj.getBioDate().toString();
			str.append(s3).append(" ");
			str.append(s4);
			if (str.toString().compareTo(lastempBiodatefromDB.toString()) == 0) {
				log.info("count" + count);
				temp = count;
			}
		}
		if(count == temp) {
			throw new FileDataAlreadyExistsException("File Data  Exists");
		}
		log.info(temp + "temp");
		for (int i = temp; i < list.size(); i++) {
			filteredlist.add(list.get(i));
		}
		return filteredlist;
	}

}
