package com.eidiko.portal.helper.employee;

import com.eidiko.portal.config.employee.SecurityUtil;
import com.eidiko.portal.entities.employee.EmpAccessLvlMapping;
import com.eidiko.portal.repo.employee.EmployeeRepo;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Set;

@Service
public class Helper {
	

	@Autowired
	private EmployeeRepo employeeRepo;

	public Date getStartDate(Date dateOfJoining) {

		Date portalStartDate = getTimestampFromatFromString(ConstantValues.PORTAL_STARTING_FROM_DATE);
		if (dateOfJoining.after(portalStartDate))
			return dateOfJoining;

		return portalStartDate;
	}

	public Date getTimestampFromatFromString(String date) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(ConstantValues.SIMPLE_DATE_FORMAT);
			java.util.Date parsedDate = dateFormat.parse(date);
			return new Date(parsedDate.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean validateStartDateAndEndDate(Date startDate, Date endDate) {
		return (endDate==null|| DateUtils.isSameDay(startDate, endDate) || endDate.after(startDate));

	}

	public boolean validateCurrentDatePresentOrNot(Date startDate, Date endDate) {
		java.util.Date date = new java.util.Date();
		return ((DateUtils.isSameDay(date, startDate) || startDate.before(date))
				&& (endDate==null || DateUtils.isSameDay(date, endDate) || endDate.after(date)));

	}
	
	public boolean validateStartingDateFromEmployeeDatabase(Date startDate, Date endDate, Date updatingDate) {		
		return ((DateUtils.isSameDay(startDate, updatingDate) || startDate.before(updatingDate))&&
				(endDate==null || DateUtils.isSameDay(startDate, endDate) || endDate.after(updatingDate)));
	}
	
	public String getModifiedByString(long empId) {
		return empId+" - "+this.employeeRepo.getEmployeeName(empId);
	}
	
	
	public boolean validateUserOrAuth() throws AuthenticationException {

		Set<EmpAccessLvlMapping> accessLvl = SecurityUtil.getCurrentUserDetails().getAccessLvl();
		for (EmpAccessLvlMapping e : accessLvl) {
			if (e.getAccessLevel().getAccessLvlName().equals("1003")
					|| e.getAccessLevel().getAccessLvlName().equals("1004")
					|| e.getAccessLevel().getAccessLvlName().equals("1005")
					|| e.getAccessLevel().getAccessLvlName().equals("1006")
					|| e.getAccessLevel().getAccessLvlName().equals("1007")
					|| e.getAccessLevel().getAccessLvlName().equals("1008")) {
				return true;
			}
		}
		return false;

	}
	
	
	
	

}
