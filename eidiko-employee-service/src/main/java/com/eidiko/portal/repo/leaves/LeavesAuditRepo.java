package com.eidiko.portal.repo.leaves;

import com.eidiko.portal.entities.leaves.EmployeeLeaveAuditStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeavesAuditRepo extends JpaRepository<EmployeeLeaveAuditStatus,Long> {
}
