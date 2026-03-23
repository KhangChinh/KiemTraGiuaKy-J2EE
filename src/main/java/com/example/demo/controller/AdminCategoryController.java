package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.repository.CourseRepository;
import com.example.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        // Tạo map đếm số course cho mỗi category
        Map<Integer, Long> courseCountMap = new HashMap<>();
        for (Category cat : categories) {
            courseCountMap.put(cat.getId(), courseRepository.countByCategoryId(cat.getId()));
        }
        model.addAttribute("categories", categories);
        model.addAttribute("courseCountMap", courseCountMap);
        return "admin/categories/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/categories/add";
    }

    @PostMapping("/add")
    public String addCategory(@ModelAttribute("category") Category category,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            model.addAttribute("error", "Tên danh mục không được để trống!");
            return "admin/categories/add";
        }
        categoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("success", "Thêm danh mục thành công!");
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục với ID: " + id));
        model.addAttribute("category", category);
        return "admin/categories/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable("id") Integer id,
                                 @ModelAttribute("category") Category category,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            model.addAttribute("error", "Tên danh mục không được để trống!");
            return "admin/categories/edit";
        }
        category.setId(id);
        categoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("success", "Cập nhật danh mục thành công!");
        return "redirect:/admin/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        long courseCount = courseRepository.countByCategoryId(id);
        if (courseCount > 0) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa danh mục đang có " + courseCount + " học phần!");
            return "redirect:/admin/categories";
        }
        categoryService.deleteCategory(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa danh mục!");
        return "redirect:/admin/categories";
    }
}
