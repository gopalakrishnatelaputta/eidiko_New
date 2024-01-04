package com.eidiko.portal.helper.employee;

public class AuthAssignedConstants {
	
	private AuthAssignedConstants() {}

	public static final String EMPLOYEE_LEVEL_ACCESS="hasAnyAuthority('1001','1002','1003','1004','1005','1006','1007','1008')";
	public static final String MANAGER_LEVEL_ACCESS="hasAnyAuthority('1003','1004','1005','1006','1007','1008')";
	public static final String HR_LEVEL_ACCESS="hasAnyAuthority('1005','1006','1007','1008')";
	public static final String ADMIN_LEVEL_ACCESS="hasAnyAuthority('1007','1008')";
	public static final String SUPER_ADMIN_LEVEL_ACCESS="hasAuthority('1008')";
	
	
}
