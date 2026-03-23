package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.model.CustomUserDetails;
import com.example.demo.service.CourseService;
import com.example.demo.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping({"/", "/home"})
    public String home(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @AuthenticationPrincipal CustomUserDetails userDetails,
                       Model model) {
        int pageSize = 5;
        Page<Course> coursePage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            coursePage = courseService.searchCoursesPage(keyword.trim(), page, pageSize);
            model.addAttribute("keyword", keyword.trim());
        } else {
            coursePage = courseService.getCoursesPage(page, pageSize);
        }

        // Lấy danh sách courseId đã đăng ký (nếu đã đăng nhập & là STUDENT)
        Set<Long> enrolledCourseIds = new HashSet<>();
        if (userDetails != null) {
            enrolledCourseIds = enrollmentService.getEnrolledCourseIds(userDetails.getStudent().getStudentId());
        }

        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("totalItems", coursePage.getTotalElements());
        model.addAttribute("enrolledCourseIds", enrolledCourseIds);

        return "home";
    }
}
