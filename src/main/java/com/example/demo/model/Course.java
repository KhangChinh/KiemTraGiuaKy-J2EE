package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 500)
    private String image;

    @Column(nullable = false)
    private Integer credits;

    @Column(length = 255)
    private String lecturer;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
