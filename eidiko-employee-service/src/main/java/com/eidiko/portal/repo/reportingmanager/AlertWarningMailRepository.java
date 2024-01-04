package com.eidiko.portal.repo.reportingmanager;

import com.eidiko.portal.entities.reportingmanager.AlertWarningMail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertWarningMailRepository extends JpaRepository<AlertWarningMail,Long> {
    AlertWarningMail findByEmpId(long empId);

    List<AlertWarningMail> findByEmpIdAndReadByLessThan(long empId, int i);
}
