package com.example.demo.controller;

import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.Student;
import com.example.demo.model.Enrollment;
import com.example.demo.model.Course;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.EnrollmentService;
import com.example.demo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseService courseService;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    @GetMapping
    public String viewProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Student student = studentRepository.findById(userDetails.getStudent().getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        model.addAttribute("student", student);

        // Determine role
        boolean isStudent = student.getRoles().stream().anyMatch(r -> r.getName().equals("STUDENT"));
        boolean isLecturer = student.getRoles().stream().anyMatch(r -> r.getName().equals("LECTURER"));
        boolean isAdmin = student.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));

        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isLecturer", isLecturer);
        model.addAttribute("isAdmin", isAdmin);

        // Enrolled courses for students
        if (isStudent) {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudentId(student.getStudentId());
            model.addAttribute("enrollments", enrollments);
        }

        // Courses taught by lecturer
        if (isLecturer) {
            List<Course> taughtCourses = courseService.getCoursesByLecturer(student.getUsername());
            model.addAttribute("taughtCourses", taughtCourses);
        }

        return "user/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @RequestParam("field") String field,
                                @RequestParam("value") String value,
                                RedirectAttributes redirectAttributes) {
        Student student = studentRepository.findById(userDetails.getStudent().getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        switch (field) {
            case "username":
                if (value == null || value.trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Tên đăng nhập không được để trống!");
                    return "redirect:/profile";
                }
                if (!value.equals(student.getUsername()) && studentRepository.existsByUsername(value)) {
                    redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã tồn tại!");
                    return "redirect:/profile";
                }
                student.setUsername(value.trim());
                break;
            case "email":
                if (value == null || value.trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Email không được để trống!");
                    return "redirect:/profile";
                }
                if (!value.equals(student.getEmail()) && studentRepository.existsByEmail(value)) {
                    redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng!");
                    return "redirect:/profile";
                }
                student.setEmail(value.trim());
                break;
            default:
                redirectAttributes.addFlashAttribute("error", "Trường không hợp lệ!");
                return "redirect:/profile";
        }

        studentRepository.save(student);
        userDetails.getStudent().setUsername(student.getUsername());
        userDetails.getStudent().setEmail(student.getEmail());

        redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        Student student = studentRepository.findById(userDetails.getStudent().getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (!passwordEncoder.matches(oldPassword, student.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu cũ không chính xác!");
            redirectAttributes.addFlashAttribute("activeTab", "security");
            return "redirect:/profile";
        }

        if (newPassword == null || newPassword.trim().length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự!");
            redirectAttributes.addFlashAttribute("activeTab", "security");
            return "redirect:/profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
            redirectAttributes.addFlashAttribute("activeTab", "security");
            return "redirect:/profile";
        }

        student.setPassword(passwordEncoder.encode(newPassword.trim()));
        studentRepository.save(student);
        userDetails.getStudent().setPassword(student.getPassword());

        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        redirectAttributes.addFlashAttribute("activeTab", "security");
        return "redirect:/profile";
    }

    @PostMapping("/upload-image")
    public String uploadImage(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) {
        if (imageFile == null || imageFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn một tệp hình ảnh!");
            return "redirect:/profile";
        }

        Student student = studentRepository.findById(userDetails.getStudent().getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        try {
            String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, imageFile.getBytes());
            student.setImage("/images/" + fileName);
            studentRepository.save(student);
            userDetails.getStudent().setImage(student.getImage());
            redirectAttributes.addFlashAttribute("success", "Cập nhật ảnh đại diện thành công!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tải ảnh lên!");
            e.printStackTrace();
        }

        return "redirect:/profile";
    }
}
