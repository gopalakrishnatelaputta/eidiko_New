package com.eidiko.portal.repo.taskstatus;

import com.eidiko.portal.entities.taskstatus.EmpLowRatingAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpLowRatingRepository extends JpaRepository<EmpLowRatingAudit, Long> {
    EmpLowRatingAudit findByEmpIdAndMonth(Long empId, int currentMonth);
}
