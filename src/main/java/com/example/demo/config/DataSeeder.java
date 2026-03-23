package com.example.demo.config;

import com.example.demo.model.Category;
import com.example.demo.model.Course;
import com.example.demo.model.Role;
import com.example.demo.model.Student;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedData(RoleRepository roleRepository,
                                      StudentRepository studentRepository,
                                      CategoryRepository categoryRepository,
                                      CourseRepository courseRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed Roles
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setName("ADMIN");
                        return roleRepository.save(r);
                    });

            Role studentRole = roleRepository.findByName("STUDENT")
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setName("STUDENT");
                        return roleRepository.save(r);
                    });

            // Seed Admin
            if (!studentRepository.existsByUsername("admin")) {
                Student admin = new Student();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@university.edu.vn");
                admin.setRoles(Set.of(adminRole));
                studentRepository.save(admin);
            }

            // Seed Student
            if (!studentRepository.existsByUsername("student1")) {
                Student student = new Student();
                student.setUsername("student1");
                student.setPassword(passwordEncoder.encode("123456"));
                student.setEmail("student1@university.edu.vn");
                student.setRoles(Set.of(studentRole));
                studentRepository.save(student);
            }

            // Seed Categories
            if (categoryRepository.count() == 0) {
                Category daiCuong = new Category(null, "Đại cương");
                Category coSoNganh = new Category(null, "Cơ sở ngành");
                Category chuyenNganh = new Category(null, "Chuyên ngành");
                categoryRepository.save(daiCuong);
                categoryRepository.save(coSoNganh);
                categoryRepository.save(chuyenNganh);

                // Seed Courses
                if (courseRepository.count() == 0) {
                    courseRepository.save(new Course(null, "Nhập môn Lập trình",
                            "https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=400&h=250&fit=crop",
                            3, "TS. Nguyễn Văn A", daiCuong));

                    courseRepository.save(new Course(null, "Cơ sở Dữ liệu",
                            "https://images.unsplash.com/photo-1544383835-bda2bc66a55d?w=400&h=250&fit=crop",
                            4, "PGS.TS. Trần Thị B", coSoNganh));

                    courseRepository.save(new Course(null, "Lập trình Web",
                            "https://images.unsplash.com/photo-1547658719-da2b51169166?w=400&h=250&fit=crop",
                            3, "ThS. Lê Văn C", chuyenNganh));

                    courseRepository.save(new Course(null, "Trí tuệ Nhân tạo",
                            "https://images.unsplash.com/photo-1677442136019-21780ecad995?w=400&h=250&fit=crop",
                            3, "TS. Phạm Văn D", chuyenNganh));

                    courseRepository.save(new Course(null, "Mạng Máy tính",
                            "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=400&h=250&fit=crop",
                            3, "ThS. Hoàng Thị E", coSoNganh));

                    courseRepository.save(new Course(null, "Cấu trúc Dữ liệu và Giải thuật",
                            "https://images.unsplash.com/photo-1515879218367-8466d910auj7?w=400&h=250&fit=crop",
                            4, "PGS.TS. Vũ Văn F", coSoNganh));

                    courseRepository.save(new Course(null, "Toán Rời rạc",
                            "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=400&h=250&fit=crop",
                            3, "TS. Đỗ Thị G", daiCuong));

                    courseRepository.save(new Course(null, "Hệ điều hành",
                            "https://images.unsplash.com/photo-1518432031352-d6fc5c10da5a?w=400&h=250&fit=crop",
                            3, "ThS. Bùi Văn H", coSoNganh));

                    courseRepository.save(new Course(null, "Lập trình Java",
                            "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400&h=250&fit=crop",
                            3, "TS. Ngô Văn I", chuyenNganh));

                    courseRepository.save(new Course(null, "An toàn Thông tin",
                            "https://images.unsplash.com/photo-1555949963-ff9fe0c870eb?w=400&h=250&fit=crop",
                            3, "PGS.TS. Lý Thị K", chuyenNganh));

                    courseRepository.save(new Course(null, "Phân tích Thiết kế Hệ thống",
                            "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=400&h=250&fit=crop",
                            3, "ThS. Trương Văn L", chuyenNganh));

                    courseRepository.save(new Course(null, "Xác suất Thống kê",
                            "https://images.unsplash.com/photo-1509228468518-180dd4864904?w=400&h=250&fit=crop",
                            3, "TS. Mai Thị M", daiCuong));
                }
            }
        };
    }
}
