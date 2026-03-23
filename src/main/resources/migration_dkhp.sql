-- =============================================
-- Migration script cho database dkhp_db
-- Course Registration Application
-- =============================================

CREATE DATABASE IF NOT EXISTS dkhp_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE dkhp_db;

-- Bảng Student
CREATE TABLE IF NOT EXISTS student (
    student_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng Role
CREATE TABLE IF NOT EXISTS role (
    role_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng liên kết Student - Role (Many-to-Many)
CREATE TABLE IF NOT EXISTS student_role (
    student_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (student_id, role_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng Category
CREATE TABLE IF NOT EXISTS category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng Course
CREATE TABLE IF NOT EXISTS course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image VARCHAR(500),
    credits INT NOT NULL DEFAULT 3,
    lecturer VARCHAR(255),
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng Enrollment
CREATE TABLE IF NOT EXISTS enrollment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    enroll_date DATE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Dữ liệu mẫu
-- =============================================

-- Roles
INSERT INTO role (name) VALUES ('ADMIN'), ('STUDENT');

-- Categories
INSERT INTO category (name) VALUES 
('Đại cương'),
('Cơ sở ngành'),
('Chuyên ngành');

-- Courses mẫu
INSERT INTO course (name, image, credits, lecturer, category_id) VALUES
('Nhập môn Lập trình', 'https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=400&h=250&fit=crop', 3, 'TS. Nguyễn Văn A', 1),
('Cơ sở Dữ liệu', 'https://images.unsplash.com/photo-1544383835-bda2bc66a55d?w=400&h=250&fit=crop', 4, 'PGS.TS. Trần Thị B', 2),
('Lập trình Web', 'https://images.unsplash.com/photo-1547658719-da2b51169166?w=400&h=250&fit=crop', 3, 'ThS. Lê Văn C', 3),
('Trí tuệ Nhân tạo', 'https://images.unsplash.com/photo-1677442136019-21780ecad995?w=400&h=250&fit=crop', 3, 'TS. Phạm Văn D', 3),
('Mạng Máy tính', 'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=400&h=250&fit=crop', 3, 'ThS. Hoàng Thị E', 2),
('Cấu trúc Dữ liệu và Giải thuật', 'https://images.unsplash.com/photo-1515879218367-8466d910auj7?w=400&h=250&fit=crop', 4, 'PGS.TS. Vũ Văn F', 2),
('Toán Rời rạc', 'https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=400&h=250&fit=crop', 3, 'TS. Đỗ Thị G', 1),
('Hệ điều hành', 'https://images.unsplash.com/photo-1518432031352-d6fc5c10da5a?w=400&h=250&fit=crop', 3, 'ThS. Bùi Văn H', 2),
('Lập trình Java', 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400&h=250&fit=crop', 3, 'TS. Ngô Văn I', 3),
('An toàn Thông tin', 'https://images.unsplash.com/photo-1555949963-ff9fe0c870eb?w=400&h=250&fit=crop', 3, 'PGS.TS. Lý Thị K', 3),
('Phân tích Thiết kế Hệ thống', 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=400&h=250&fit=crop', 3, 'ThS. Trương Văn L', 3),
('Xác suất Thống kê', 'https://images.unsplash.com/photo-1509228468518-180dd4864904?w=400&h=250&fit=crop', 3, 'TS. Mai Thị M', 1);

-- Assign roles
INSERT INTO student_role (student_id, role_id) VALUES (1, 1); -- admin -> ADMIN
INSERT INTO student_role (student_id, role_id) VALUES (2, 2); -- student1 -> STUDENT
