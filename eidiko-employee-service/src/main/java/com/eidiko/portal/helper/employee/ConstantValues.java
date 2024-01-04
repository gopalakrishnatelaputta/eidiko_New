package com.eidiko.portal.helper.employee;

import java.net.URI;

public class ConstantValues {

	// LeavesBeta Service
    public static final String CREATED = "created";
    public static final String UPDATED = "updated";
    public static final String DATE_MISMATCH = "TO date should be after FROM date";
	public static final String FILE_NAME = "fileName";
	public static final String NOT_ALLOWED_EXTENSIONS = "Please provide files in pdf, JPEG, png or docx format";
	public static final String ATTACHMENTS = "attachments";
	public static final String LEAVE_APPLIED = "Leave applied for given date: ";
	public static final String APPLY_LEAVE_MAIL_TXT = "apply-leave-mail.txt";
	public static final String DEFAULT_CONTACT_NO = "1234567890";
	public static final String TEMPLATES = "templates/";
	public static final String DELETE_LEAVE_MAIL_TXT = "delete-leave-mail.txt";
	public static final String LEAVE_NOT_FOUND = "leave not found with this id : ";
    public static final String APPROVE_LEAVE_MAIL_TXT = "approve-leave-mail.txt";
	public static final String LEAVES_MAIL_ID = "Leaves@eidiko.com";
	public static final String HR_TEAM_MAIL_ID = "Hrteam@eidiko.com";
    public static final String EXTENSION = "fileExtension";

    private ConstantValues() {
	}

	public static final String URL_SEPARATOR = "/";
	public static final String QUERY_PARAM = "?";
	public static final String QUERY_PARAM_SEPARATOR = "&";
	public static final String QUERY_PARAM_ASSIGN = "=";
	public static final String PAGE_NO = "pageNo";
	public static final String PAGE_SIZE = "pageSize";
	public static final String SORT_BY = "sortBy";
	public static final String NO = "no";

//	 URLS for Other microservices
	//--------------------------------------------------------------------------------------------
	//-------- EMployee Service ------------------------
	public static final String EMPLOYEE_SERVICE_BASE_URL = "http://localhost:9190/api/v1";
	
//	-------------------------------------------------------------------------------------------
	// Task Details Services
	public static final String TASK_STATUS_SERVICE_BASE_URL = "http://localhost:9190/api/V1/dailyStatusReport";
	public static final String TASK_STATUS_SERVICE_GET_REPORT_FROMDATE_TODATE_RESOURCE = "get";
	public static final String TASK_STATUS_SERVICE_ADD_REPORT_FROMDATE_TODATE_RESOURCE = "add";
	public static final String TASK_STATUS_SERVICE_UPDATE_REPORT_FROMDATE_TODATE_RESOURCE = "updateAll";

	public static final String TASK_STATUS_SERVICE_PENDING_REPORT_FROMDATE_TODATE_RESOURCE = "Pending";

	public static final String TASK_STATUS_SERVICE_STATUS_AND_PAGENO_RESOURCE = "?status=No&pageNo=0";

	public static final String TASK_STATUS_SERVICE_STATUS_RESOURCE = "Status";
	public static final String TASK_STATUS_SERVICE_ALL_PENDING_STATUS = "AllPendingStatus";

	public static final String TASK_STATUS_SERVICE_ALL_PENDING_REPORTS = "AllPendingReports";

// ---------------------------------------------------------------------------------------------
	
// ----------------------------------------------------------------------------------------------
	// Rating Service
	public static final String RATING_SERVICE_BASE_URL = "http://localhost:9190/api/V1/rating";
	public static final String RATING_GET_ALL_RATINGS_BY_YEAR = "get-all-rating-by-year";
	public static final String RATING_GET_ALL_RATINGS_BY_MONTH_YEAR = "get-rating-by-month-and-year";
	
//---------------------------------------------------------------------------------------------------
	//EmpTrackingService
	
	public static final String EMP_SKILL_TRACKING_SERVICE_BASE_URL = "http://localhost:9190/api/V1/emp-skills-tracking"; 
	public static final String EMP_SKILL_TRACKING_SERVICE_RESOURCE_INSERT = "insert"; 
	public static final String EMP_SKILL_TRACKING_SERVICE_QUERYPARAM_SKILL = "skill"; 
	public static final String EMP_SKILL_TRACKING_SERVICE_QUERYPARAM_WORKING = "working"; 
	public static final String EMP_SKILL_TRACKING_SERVICE_RESOURCE_UPDATE = "update"; 
	
	
	
	
//  -------------------------------------------------------------------------------------------------
	// Biometric-Service

	public static final String BIOMETRIC_SERVICE_BASE_URL = "http://localhost:9190/api/V1/biometric";

	public static final String BIOMETRIC_GET_REPORT_FROMDATE_TODATE_BYEMPID = "getBiometricReports-fromDatetotodate-forEmp";
	public static final String BIOMETRIC_GET_ALL_BYDATE = "getAll-BiometricReports-ByDate";
	public static final String BIOMETRIC_GET_BYMONTH_YEAR = "get-BiometricReports-ByMonthAndisLate";

	public static final String BIOMETRIC_UPDATE_LATE_REPORT_RESOURCE = "update-islate-report";
	public static final String BIOMETRIC_GET_BYMONTH_ANDISMONTH = "get-ByMonthAndisLate";
	public static final String BIOMETRIC_GET_BIOMETRIC_DATA_BY_ID_DATE = "biometricDatabyIdDate";

	public static final String BIOMETRIC_POST_FILE_UPLOAD_RESOURCE = "fileupload";

	public static final String EMPLOYEES_CALCULATE_REPORT_RESOURCE = "employees-calculated-report";
	
	public static final String BIOMETRIC_GET_REPORTS_VIEW = "get-BiometricReports-view";

	public static final String UPDATE_MISSING_BIOMETRIC_REPORT_RESOURCE = "update-employee-missing-report";
	
	
	public static final String GET_EMPLOYEE_MISSING_REPORT_BY_YEAR = "get-missing-report-employee";
	
	public static final String GET_ALL_EMPLOYEES_MISSING_REPORT_MONTHLY = "get-missing-report-employees-monthly";
	public static final String GET_ALL_EMPLOYEES_MISSING_REPORT_YEARLY = "get-missing-report-employees-yearly";
	// Leaves-Service

	public static final String LEAVES_SERVICE_BASE_URL = "http://localhost:9190/api/V1/leaves";
	
	public static final String LEAVES_SERVICE_UPLOAD_ATTENDANCE_RESOURCE = "attendance/upload-xls";
	public static final String LEAVES_SERVICE_UPLOAD_LEAVES_PER_BAND_RESOURCE = "add-leave-as-per-band";
	
	public static final String LEAVES_SERVICE_GET_EMP_LEAVE_COUNT_RESOURCE = "get-spent-leave-count";
	
	public static final String LEAVES_SERVICE_GET_EMP_LEAVE_STATUS_REPORT_RESOURCE = "get-employee-leave-status-report";
	public static final String LEAVES_SERVICE_GET_EMP_LEAVE_STATUS_REPORT_MONTHLY_RESOURCE = "get-employee-leave-status-report-monthly";

	// ---------------------------------Reporting Manager Service --------------------------------------------
	
	public static final String REPORTING_MANAGER_SERVICE_BASE_URL = "http://localhost:9190/api/V1/reporting-manager";
	
	public static final String REPORTING_MANAGER_SERVICE_GET_REPORTING_EMP_RESOURCE = "get-reporting-employees";
	
	public static final String REPORTING_MANAGER_SERVICE_GET_DETAILED_INFO_FOR_EMP = "get-reporting-employee/detailed-information";
	
	
	// ------------------------------------------------------------------------------------------------------------------
						// Caution Mail Service
	
	public static final String CAUTION_MAIL_SERVICE_BASE_URL = "http://localhost:9190/api/V1/warningMails";
	public static final String CAUTION_MAIL_SERVICE_CONTEXT_SENT_MAIL = "sent-mail";
	public static final String CAUTION_MAIL_SERVICE_CONTEXT_GET_BY_EMPID = "get-by-empId";
	public static final String CAUTION_MAIL_SERVICE_CONTEXT_GET_BY_MONTH_YEAR = "get-by-monthAndYear";
	public static final String CAUTION_MAIL_SERVICE_CONTEXT_GET_ALL_BY_YEAR = "get-all-by-year";
	public static final String CAUTION_MAIL_SERVICE_CONTEXT_GET_REPORTS_BY_EMPID = "get-all-caution-mail-report-for-employee";
	public static final String CAUTION_MAIL_SERVICE_CONTEXT_GET_ALL_CAUTION_REPORTS = "get-all-caution-mail-report";
	
	
	// ------------------------------------------------------------------------------------------------------------------

	public static final String PROFILE_DOCUMENTS_EIDIKO_PORTAL = "profiles";
	public static final String DOCUMENTS_EIDIKO_PORTAL = "templates/";
//	public static final String DOCUMENTS_EIDIKO_PORTAL = "C:/eidikoportal/templates/";

	public static final String JWT_SECRET = "eidiko-internal-portal";

	public static final long TOKEN_VALIDITY = 1000 * 60 * 60 * 24;

	public static final URI ERROR_MESSAGE_URL = URI.create("/error");

	public static final String ERROR_MESSAGE = "error";

	public static final String SUCCESS_MESSAGE = "success";

	public static final String MESSAGE = "message";

	public static final String STATUS_CODE = "statusMessage";

	public static final String FORGOT_PASSWORD_MAIL_TEMPLATE_FILE = "forgot-password-mail";

	public static final String ACTIVE_FLAG = "Active";

	public static final String INACTIVE_FLAG = "Inactive";

	public static final String FORGOT_PASSWORD_MAIL_SUBJECT = "Eidiko Portal Password";

	public static final String STATUS_TEXT = "status";

	public static final String PASSWORD_SENT_TEXT_MAIL = "password sent to your mailId : ";
	public static final String PASSWORD_UPDATED_TEXT = "Password Updated Successfully";

	public static final String EMPLOYEE_CREATED_SUCCESS_TEXT = "Employee created Successfully";

	public static final String EMPLOYEE_CREATED_FAIL_TEXT = "Employee not created";

	public static final String DATA_FETCHED_SUCCESS_TEXT = "Data fetched successfully";

	public static final String NO_DATA_FETCHED_SUCCESS_TEXT = "No Data Available";

	public static final String RESULT = "result";

	public static final int EMPLOYEE_ROLE = 101;
	public static final int EMPLOYEE_ACCESS_LEVEL = 1001;

	public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss.SSS";
	public static final String PORTAL_STARTING_FROM_DATE = "2023-01-01 00:00:00.0";
	public static final String TOKEN_EXPIRE_TIME = "tokenExpireTime";
	public static final String TOKEN_EXPIRE_TIME_IN_MILLS = "tokenExpireTimeInMills";
	public static final String USER_NAME = "userName";
	public static final String USER_ROLE = "userRole";
	public static final String USER_IS_DISABLED = "User Is Disabled";
	public static final String BAD_CREDENTIALS = "Bad Credentials";
	public static final String USER_NOT_FOUND_WITH_THIS_ID = "User Not Found With This Id";
	public static final String EMPLOYEE_IS_ALREADY_PRESENT_WITH_ID = "Employee Is Already Present with ";
	public static final String PASSWORD_NOT_UPDATED_PLEASE_TRY_AGAIN = "Password Not Updated! Please Try Again";
	public static final String MANAGER_NOT_FOUND_WITH_ID = "Manager Not Found With Id";
	public static final String ACCESS_LEVEL_NOT_FOUND = "Access Level Not Found";
	public static final String ROLE_NOT_FOUND = "Role Not Found";
	public static final String YOUR_NOT_UNABLE_TO_CHANGE_THE_PASSWORD_FOR_THIS_ID = "You are Not allowed To Change The Password For This Id.";
	public static final String NEW_PASSWORD_AND_CONFIRM_PASSWORD_ARE_NOT_MATCHING = "New Password And Confirm Password Are Not Matching.";
	public static final String OLD_PASSWORD_IS_NOT_MATCHING = "Old Password Is Not Matching";
	public static final String REPORTING_MANAGERS = "reportingManagers";
	public static final String REPORTED_EMPLOYEES = "reportedEmployees";
	public static final String EMP_ID = "empId";
	public static final String EMAIL_ID = "emailId";
	public static final String EMPLOYEE_FETCHED_SUCCESSFULLY = "Employee Fetched Successfully";
	public static final String AUTHORITIES = "Authorities";
	public static final String DESIGNATION = "Designation";
	public static final String PATH = "Path";
	public static final String AUTHORIZATION = "Authorization";
	public static final String JWT_TOKEN_DOES_NOT_BEGIN_WITH_BEARER = "Jwt Token Does Not Begin With Bearer";
	public static final String INVALID_TOKEN_JWT = "Invalid Token Jwt";
	public static final String JWT_TOKEN_NOT_VALID = "Jwt Token Not Valid";
	public static final String GENERATING_JWT_TOKEN_FOR_USERNAME = "Generating Jwt Token For Username{}";
	public static final String STAR = "*";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String ACCEPT = "Accept";
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String DELETE = "DELETE";
	public static final String PUT = "PUT";
	public static final String OPTIONS = "OPTIONS";
	public static final String PATTERN = "/**";
	public static final String SESSION_HAS_BEEN_EXPIRED = "Session Has Been Expired";

	public static final String EMAIL_TEMPLATE_MAIL_FILE_FOR_FORGOT_PASSWORD_NOT_FOUND = "Email Template Mail File For Forgot Password Not Found";
	public static final String SOMETHING_WENT_WRONG_PLEASE_TRY_AGAIN_AFTER_SOMETIME = "Something Went Wrong! Please Try Again After Sometime";
	public static final String EMP_NAME = "empName";
	public static final String PASSWORD_TEXT = "Password-Text";
	public static final String MAIL_SENT = "Mail Sent";
	public static final String CUSTOM_USER_DETAILS = "Custom User Details";
	public static final String SET_SHIFT_START_TIME = "10:00";
	public static final String SET_SHIFT_END_TIME = "19:00:00";
	public static final String USER_IS_ALREADY_PRESENT = "User Is Already Present";
	public static final String PERMISSION_DENIED = "Permission Denied";
	public static final String CLIENT_LOCATION = "CLIENT_LOCATION";
	public static final String CLIENT_LOCATION2 = "Client location";
	public static final String WFO = "WFO";
	public static final String EMPLOYEE_NOT_FOUND_WITH = "Employee Not Found With";
	public static final String BEARER = "Bearer";
	public static final String PROCESSED_SUCCESSFULLY = "Process Successfully";
	public static final String NOT_DELETED = "Not Deleted";
	public static final String NOT_UPDATED = "Not Updated";
	public static final String DEFAULT_ABOUT_TEXT = "Software engineers apply principles and techniques of engineering, mathematics, and computer science to the design, development, and testing of software applications for computers.";

	public static final String EMPLOYEE_ID_MUST_NOT_BE_NULL_OR_BLANK = "Employee Id must not blank or null";
	public static final String WEEKOFF_CAN_ONLY_BE_USED_FOR_TWO_DAYS = "WeekOff can only be used on two days.";
	public static final String EMPLOYEE_AND_MANAGER_SHOULD_NOT_BE_THE_SAME = "Employee and manager should not be the same";
	public static final String TOKEN = "token";
	public static final String PLEASE_ENTER_VALID_CONTACT_NUMBER = "Please Enter valid Contact Number";
	public static final String BACKEND_CONNECTION_ERROR = "Backend Connection error";
	public static final String REPORTING_MANAGER_ID = "reportingManagerId";
	public static final String REPORTING_MANAGER_NAME = "reportingManagerName";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String MODIFIED_BY = "modifiedBy";
	public static final String MODIFIED_DATE = "modifiedDate";
	public static final String REPORTING_EMPLOYEE_ID = "reportedEmployeeId";
	public static final String REPORTING_EMPLOYEE_NAME = "reportedEmployeeName";
	public static final String NOT_PROCESSED = "Not Processed";
	public static final String CONTACT_UPDATED_SUCCESSFULLY = "Contact Updated Successfully";
	public static final String APPLICATION_STARTED = "Application started !!!";
	public static final String ACCESS_LEVEL_IS_ALREADY_PRESENT = "AccessLvl is already present";

}
