package com.eidiko.portal.repo.employee;

import com.eidiko.portal.entities.employee.Designations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignationRepo extends JpaRepository<Designations,Integer>{
	
	
	public Designations findByDesignationName(String name);
	

}
