package com.example.demo.repository;

import com.example.demo.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentStudentId(Long studentId);
    List<Enrollment> findByCourseId(Long courseId);
    boolean existsByStudentStudentIdAndCourseId(Long studentId, Long courseId);
}
