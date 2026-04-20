package com.andrei.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "students")
@DiscriminatorValue("STUDENT")
public class Student extends Person {
    @Column(name = "registration_number", unique = true)
    private String registrationNumber;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments;
}
