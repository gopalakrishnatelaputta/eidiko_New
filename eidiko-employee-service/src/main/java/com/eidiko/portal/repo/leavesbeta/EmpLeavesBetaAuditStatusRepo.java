package com.eidiko.portal.repo.leavesbeta;

import com.eidiko.portal.entities.leavesbeta.EmpLeavesBetaAuditStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpLeavesBetaAuditStatusRepo extends JpaRepository<EmpLeavesBetaAuditStatus, Long> {
}
