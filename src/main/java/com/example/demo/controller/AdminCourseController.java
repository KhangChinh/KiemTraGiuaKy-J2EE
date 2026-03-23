package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.service.CategoryService;
import com.example.demo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/admin/courses")
public class AdminCourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    // Danh sách courses (có tìm kiếm)
    @GetMapping
    public String listCourses(@RequestParam(value = "keyword", required = false) String keyword,
                              Model model) {
        List<Course> courses;
        if (keyword != null && !keyword.trim().isEmpty()) {
            courses = courseService.searchCourses(keyword.trim());
            model.addAttribute("keyword", keyword.trim());
        } else {
            courses = courseService.getAllCourses();
        }
        Map<Long, Long> enrollCountMap = new HashMap<>();
        for (Course c : courses) {
            enrollCountMap.put(c.getId(), enrollmentRepository.countByCourseId(c.getId()));
        }
        model.addAttribute("courses", courses);
        model.addAttribute("enrollCountMap", enrollCountMap);
        return "admin/courses/list";
    }

    // Form thêm course
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/courses/add";
    }

    // Xử lý thêm course
    @PostMapping("/add")
    public String addCourse(@ModelAttribute("course") Course course,
                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        // Validation
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            model.addAttribute("error", "Tên học phần không được để trống!");
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/courses/add";
        }
        if (course.getCredits() == null || course.getCredits() < 1) {
            model.addAttribute("error", "Số tín chỉ phải từ 1 trở lên!");
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/courses/add";
        }

        // Xử lý upload hình ảnh
        handleImageUpload(course, imageFile);

        courseService.saveCourse(course);
        redirectAttributes.addFlashAttribute("success", "Thêm học phần thành công!");
        return "redirect:/admin/courses?added";
    }

    // Form sửa course
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học phần với ID: " + id));
        model.addAttribute("course", course);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/courses/edit";
    }

    // Xử lý sửa course
    @PostMapping("/edit/{id}")
    public String updateCourse(@PathVariable("id") Long id,
                               @ModelAttribute("course") Course course,
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        // Validation
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            model.addAttribute("error", "Tên học phần không được để trống!");
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/courses/edit";
        }
        if (course.getCredits() == null || course.getCredits() < 1) {
            model.addAttribute("error", "Số tín chỉ phải từ 1 trở lên!");
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/courses/edit";
        }

        // Xử lý upload hình ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            handleImageUpload(course, imageFile);
        } else if (course.getImage() == null || course.getImage().trim().isEmpty()) {
            // Giữ hình ảnh cũ nếu không upload file mới và không nhập URL
            Course existing = courseService.getCourseById(id).orElse(null);
            if (existing != null) {
                course.setImage(existing.getImage());
            }
        }

        course.setId(id);
        courseService.saveCourse(course);
        redirectAttributes.addFlashAttribute("success", "Cập nhật học phần thành công!");
        return "redirect:/admin/courses?updated";
    }

    // Xóa course
    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        long enrollCount = enrollmentRepository.countByCourseId(id);
        if (enrollCount > 0) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa học phần đang có " + enrollCount + " sinh viên đăng ký!");
            return "redirect:/admin/courses";
        }
        courseService.deleteCourse(id);
        return "redirect:/admin/courses?deleted";
    }

    // Helper: Xử lý upload hình ảnh
    private void handleImageUpload(Course course, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, imageFile.getBytes());
                course.setImage("/images/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
