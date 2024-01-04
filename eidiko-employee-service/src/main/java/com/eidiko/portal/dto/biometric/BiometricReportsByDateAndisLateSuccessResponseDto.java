package com.eidiko.portal.dto.biometric;

public class BiometricReportsByDateAndisLateSuccessResponseDto {
	
	private Long empId;
	private int countOfIsLate;
	public Long getEmpId() {
		return empId;
	}
	public void setEmpId(Long empId) {
		this.empId = empId;
	}
	public int getCountOfIsLate() {
		return countOfIsLate;
	}
	public void setCountOfIsLate(int countOfIsLate) {
		this.countOfIsLate = countOfIsLate;
	}
	

}
