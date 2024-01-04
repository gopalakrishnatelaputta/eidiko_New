package com.eidiko.portal.repo.taskstatus;

import com.eidiko.portal.entities.taskstatus.EmpSkillsTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpSkillsTrackingRepository extends JpaRepository<EmpSkillsTracking, Long> {
    List<EmpSkillsTracking> findAllByEmpId(long empId);

    List<EmpSkillsTracking> findBySkillsAndWorking(String skill, String working);

    List<EmpSkillsTracking> findAllByWorking(String working);

    List<EmpSkillsTracking> findAllByWorkingIgnoreCase(String workingValue);
}
