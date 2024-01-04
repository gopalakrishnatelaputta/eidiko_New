package com.eidiko.portal.helper.employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eidiko.portal.entities.employee.Employee;
import com.eidiko.portal.entities.employee.ResignedEmployee;
import com.eidiko.portal.repo.employee.EmployeeRepo;
import com.eidiko.portal.repo.employee.ResignedEmpRepo;

@Service
public class FilterResignedEmp {
	
	@Autowired
	private EmployeeRepo employeeRepo;
	
	@Autowired
	private ResignedEmpRepo resignedEmpRepo;

	public List<Employee> filterResignedEmployees() {
	    List<Employee> allEmployees = employeeRepo.findAll();
	    List<Employee> filteredData = new ArrayList<>();

	    for (Employee e : allEmployees) {
	        Long empId2 = e.getEmpId();
	        boolean existsByEmpId = resignedEmpRepo.existsByempId(empId2);
	        if (!existsByEmpId) {
	            filteredData.add(e);
	        }
	    }
	    
	    return filteredData;
	}

	 
}
