package com.example.demo.controller;

import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String viewProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Student student = studentRepository.findById(userDetails.getStudent().getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        model.addAttribute("student", student);
        return "profile";
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
            case "password":
                if (value == null || value.trim().length() < 6) {
                    redirectAttributes.addFlashAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
                    return "redirect:/profile";
                }
                student.setPassword(passwordEncoder.encode(value.trim()));
                break;
            default:
                redirectAttributes.addFlashAttribute("error", "Trường không hợp lệ!");
                return "redirect:/profile";
        }

        studentRepository.save(student);

        // Cập nhật lại thông tin trong session
        userDetails.getStudent().setUsername(student.getUsername());
        userDetails.getStudent().setEmail(student.getEmail());
        userDetails.getStudent().setPassword(student.getPassword());

        redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
        return "redirect:/profile";
    }
}
