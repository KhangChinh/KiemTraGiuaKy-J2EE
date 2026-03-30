package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.Student;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
public class AuthController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(Authentication authentication) {
        // Nếu đã đăng nhập, chuyển về home
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        return "auth/login";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "auth/403";
    }

    @GetMapping("/register")
    public String showRegisterForm(Authentication authentication) {
        // Nếu đã đăng nhập, chuyển về home
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam("confirmPassword") String confirmPassword,
                           Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "auth/register";
        }

        if (studentRepository.existsByUsername(username)) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "auth/register";
        }

        if (studentRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email đã được sử dụng!");
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "auth/register";
        }

        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new RuntimeException("Role STUDENT not found"));

        Student student = new Student();
        student.setUsername(username);
        student.setEmail(email);
        student.setPassword(passwordEncoder.encode(password));
        student.setRoles(Set.of(studentRole));
        studentRepository.save(student);

        return "redirect:/login?registered";
    }
}
