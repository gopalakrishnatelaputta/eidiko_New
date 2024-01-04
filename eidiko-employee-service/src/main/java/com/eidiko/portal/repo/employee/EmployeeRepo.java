package com.eidiko.portal.repo.employee;

import com.eidiko.portal.entities.employee.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {

	public Page<Employee> findAllByIsDeleted(boolean isDeleted, Pageable pageable);

	public List<Employee> findAllByEmpNameContaining(String empName);

	@Query(value = "SELECT * FROM employee e WHERE (e.emp_id LIKE %?1%  OR e.emp_name LIKE %?1% ) AND e.is_deleted=true", nativeQuery = true)
	public List<Employee> searchEmployee(String key);

	@Query(value = "SELECT e.emp_name FROM employee e WHERE e.emp_id = ?1", nativeQuery = true)
	public String getEmployeeName(long key);
	
	@Query(value = "SELECT e.* FROM employee e JOIN emp_access_lvl_mapping m ON e.EMP_ID = m.EMP_ID JOIN access_lvl a ON m.ACCESS_LVL = a.ACCESS_LVL_ID WHERE a.ACCESS_LVL_ID = ?1", nativeQuery = true)
	public List<Employee> getEmployeeByAccessLvl(long accessLvlId);

	List<Employee> findByEmpId(Long empId);
	List<Employee>findAllByIsDeleted(boolean isDeleted);
}
