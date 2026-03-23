package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public Page<Course> getCoursesPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return courseRepository.findAll(pageable);
    }

    public Page<Course> searchCoursesPage(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return courseRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    public Page<Course> searchCoursesPage(String keyword, Integer categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCategory = categoryId != null;

        if (hasKeyword && hasCategory) {
            return courseRepository.findByNameContainingIgnoreCaseAndCategoryId(keyword.trim(), categoryId, pageable);
        } else if (hasKeyword) {
            return courseRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
        } else if (hasCategory) {
            return courseRepository.findByCategoryId(categoryId, pageable);
        } else {
            return courseRepository.findAll(pageable);
        }
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> searchCourses(String keyword) {
        return courseRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}
