package com.andrei.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Data
@Table(name = "student")
@EqualsAndHashCode(callSuper = true)
public class Student extends Person {
    @Column(name = "registration_number", unique = true)
    private String registrationNumber;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments;
}
