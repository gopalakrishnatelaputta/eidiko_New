package com.eidiko.portal.repo.leavesbeta;

import com.eidiko.portal.entities.leavesbeta.LeaveTypesBeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveTypesBetaRepo extends JpaRepository<LeaveTypesBeta, Long> {
    LeaveTypesBeta findByLeaveCode(String leaveType);
}
