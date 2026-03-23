package com.example.demo.service;

import com.example.demo.model.Enrollment;
import com.example.demo.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public List<Enrollment> getEnrollmentsByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentStudentId(studentId);
    }

    public boolean isEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentStudentIdAndCourseId(studentId, courseId);
    }

    public Enrollment save(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    public void deleteById(Long id) {
        enrollmentRepository.deleteById(id);
    }

    public Set<Long> getEnrolledCourseIds(Long studentId) {
        return enrollmentRepository.findByStudentStudentId(studentId)
                .stream()
                .map(e -> e.getCourse().getId())
                .collect(Collectors.toSet());
    }
}
