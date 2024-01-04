package com.eidiko.portal.repo.employee;

import com.eidiko.portal.entities.employee.ClientLocations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientLocationRepo extends JpaRepository<ClientLocations, Integer> {

}
