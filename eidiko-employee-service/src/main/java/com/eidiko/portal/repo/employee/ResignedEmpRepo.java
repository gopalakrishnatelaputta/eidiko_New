package com.eidiko.portal.repo.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eidiko.portal.entities.employee.ResignedEmployee;
@Repository
public interface ResignedEmpRepo extends JpaRepository<ResignedEmployee, Long> {

	boolean existsByempId(long empId);

}
