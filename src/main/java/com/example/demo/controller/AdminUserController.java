package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.Student;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/users/add";
    }

    @PostMapping("/add")
    public String addUser(@RequestParam("username") String username,
                          @RequestParam("email") String email,
                          @RequestParam("password") String password,
                          @RequestParam("confirmPassword") String confirmPassword,
                          @RequestParam(value = "roleId", required = false) Long roleId,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        if (roleId == null) {
            model.addAttribute("error", "Vui lòng chọn một vai trò!");
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "admin/users/add";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "admin/users/add";
        }

        if (studentRepository.existsByUsername(username)) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "admin/users/add";
        }

        if (studentRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email đã được sử dụng!");
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "admin/users/add";
        }

        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) {
            model.addAttribute("error", "Vai trò không hợp lệ!");
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "admin/users/add";
        }

        Student student = new Student();
        student.setUsername(username);
        student.setEmail(email);
        student.setPassword(passwordEncoder.encode(password));
        student.setRoles(Set.of(roleOpt.get()));

        studentRepository.save(student);
        redirectAttributes.addFlashAttribute("success", "Tạo tài khoản \"" + username + "\" thành công!");
        return "redirect:/admin/courses";
    }
}
