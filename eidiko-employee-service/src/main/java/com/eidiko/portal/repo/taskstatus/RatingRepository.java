package com.eidiko.portal.repo.taskstatus;

import com.eidiko.portal.entities.taskstatus.EmpReviewRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<EmpReviewRating,Long> {

    List<EmpReviewRating> findAllByEmpId(long empId);

    List<EmpReviewRating> findAllByEmpIdAndMonth(long empId, int month);

    List<EmpReviewRating> findAllByMonth(int month);

    List<EmpReviewRating> findAllByEmpIdAndMonthAndYear(long empId, int month, int year);

    List<EmpReviewRating> findAllByEmpIdAndYear(long empId, int year);

    List<EmpReviewRating> findAllByMonthAndYear(int month, int year);

    List<EmpReviewRating> findAllByYear(int year);

    EmpReviewRating findByEmpIdAndMonth(long empId, int i);
}
