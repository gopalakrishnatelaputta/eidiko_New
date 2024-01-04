package com.eidiko.portal.repo.employee;

import com.eidiko.portal.entities.employee.Teams;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepo extends JpaRepository<Teams, Integer> {

}
