package com.eidiko.portal.repo.leaves;

import com.eidiko.portal.entities.leaves.EmployeeLeaveStatusLeaves;
import com.eidiko.portal.entities.leaves.interfaces.Employee;
import com.eidiko.portal.entities.leaves.interfaces.LeaveStatusCounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeLeaveStatusRepo extends JpaRepository<EmployeeLeaveStatusLeaves, Long> {

	public static final String GET_EMP_LEAVE_STATUS_COUNT_QUERY = "SELECT COUNT(*) as count \r\n"
			+ " , l.emp_id , e.emp_name FROM `employee_leave_status` l,employee e WHERE (e_status = 'L' OR e_status = 'l') AND l.leave_date BETWEEN ?1 AND ?2  AND  l.emp_id = e.emp_id GROUP BY l.emp_id ORDER BY COUNT(*)\r\n"
			+ " DESC";

	public static final String GET_EMP_ABSENT_STATUS_COUNT_QUERY = "SELECT COUNT(*) as count \r\n"
			+ " , l.emp_id , e.emp_name FROM `employee_leave_status` l,employee e WHERE (e_status = 'A' OR e_status = 'a') AND l.leave_date BETWEEN ?1 AND ?2 AND  l.emp_id = e.emp_id GROUP BY l.emp_id ORDER BY COUNT(*)\r\n"
			+ " DESC";
	public static final String GET_EMP_COMPOFF_STATUS_COUNT_QUERY = "SELECT COUNT(*) as count \r\n"
			+ " , l.emp_id , e.emp_name FROM `employee_leave_status` l,employee e WHERE (e_status = 'C' OR e_status = 'c') AND l.leave_date BETWEEN ?1 AND ?2 AND  l.emp_id = e.emp_id GROUP BY l.emp_id ORDER BY COUNT(*)\r\n"
			+ " DESC";

	public static final String GET_ALL_EMPLOYEE = "select emp_id,emp_name from employee where is_deleted=true";

	List<EmployeeLeaveStatusLeaves> findAllByEmpId(long empId);

	@Query(value = "SELECT e.emp_name FROM employee e WHERE e.emp_id = ?1", nativeQuery = true)
	public String getEmployeeName(long key);

	@Query(value = GET_EMP_LEAVE_STATUS_COUNT_QUERY, nativeQuery = true)
	public List<LeaveStatusCounts> getEmpLeaveStatusCount(String fromDate, String toDate);

	@Query(value = GET_EMP_ABSENT_STATUS_COUNT_QUERY, nativeQuery = true)
	public List<LeaveStatusCounts> getEmpAbsentStatusCount(String fromDate, String toDate);

	@Query(value = GET_EMP_COMPOFF_STATUS_COUNT_QUERY, nativeQuery = true)
	public List<LeaveStatusCounts> getEmpCompOffStatusCount(String fromDate, String toDate);

	@Query(value = GET_ALL_EMPLOYEE, nativeQuery = true)
	public List<Employee> getAllEmployee();

	@Query(value = " SELECT `leave_status_id`\r\n" + "    FROM `employee_leave_status`\r\n"
			+ "    WHERE `leave_status_id` NOT IN\r\n" + "    (\r\n" + "SELECT MAX(`leave_status_id`)\r\n"
			+ "        FROM `employee_leave_status`\r\n" + "        GROUP BY emp_id, leave_date\r\n" + "        )\r\n"
			+ "        ", nativeQuery = true)
	public List<Long> getEmployeeLeaveStatusDuplicate();
	
	@Query(value = "select * from employee_leave_status where emp_id=?3 and leave_date between ?1 and ?2", nativeQuery = true)
	public List<EmployeeLeaveStatusLeaves> findAllByEmpIdInThisYear(String fromDate,String toDate,long empId); 

	 EmployeeLeaveStatusLeaves findByEmpIdAndLeaveDate(long empId,Date leaveDate);

	List<EmployeeLeaveStatusLeaves> findAllByLeaveDateBetween(LocalDate fromDate, LocalDate toDate);

	//EmployeeLeaveStatusDto save(EmployeeLeaveStatusDto leaveStatus);
}
