package com.example.demo.repository;

import com.example.demo.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findByNameContainingIgnoreCase(String name, Pageable pageable);
    List<Course> findByNameContainingIgnoreCase(String name);
    Page<Course> findByCategoryId(Integer categoryId, Pageable pageable);
    Page<Course> findByNameContainingIgnoreCaseAndCategoryId(String name, Integer categoryId, Pageable pageable);
}
