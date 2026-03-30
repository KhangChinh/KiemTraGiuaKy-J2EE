package com.example.demo.controller;

import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.Enrollment;
import com.example.demo.model.Course;
import com.example.demo.model.Student;
import com.example.demo.service.CourseService;
import com.example.demo.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/enroll")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseService courseService;

    // Đăng ký học phần
    @PostMapping("/{courseId}")
    public String enrollCourse(@PathVariable("courseId") Long courseId,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        Student student = userDetails.getStudent();

        // Kiểm tra đã đăng ký chưa
        if (enrollmentService.isEnrolled(student.getStudentId(), courseId)) {
            redirectAttributes.addFlashAttribute("error", "Bạn đã đăng ký học phần này rồi!");
            return "redirect:/home";
        }

        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học phần!"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollDate(LocalDate.now());
        enrollmentService.save(enrollment);

        redirectAttributes.addFlashAttribute("success", "Đăng ký học phần \"" + course.getName() + "\" thành công!");
        return "redirect:/home";
    }

    // Trang My Courses
    @GetMapping("/my-courses")
    public String myCourses(@AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model) {
        Student student = userDetails.getStudent();
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudentId(student.getStudentId());
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("student", student);
        return "user/my-courses";
    }

    // Hủy đăng ký
    @PostMapping("/cancel/{enrollmentId}")
    public String cancelEnrollment(@PathVariable("enrollmentId") Long enrollmentId,
                                   RedirectAttributes redirectAttributes) {
        enrollmentService.deleteById(enrollmentId);
        redirectAttributes.addFlashAttribute("success", "Đã hủy đăng ký học phần!");
        return "redirect:/enroll/my-courses";
    }
}
