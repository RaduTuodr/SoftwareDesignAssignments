package com.andrei.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "student")
@EqualsAndHashCode(callSuper = true)
public class Student extends Person {
    @Column(name = "registration_number", unique = true)
    private String registrationNumber;

    @Column(name = "graduation_year")
    private Integer graduationYear;
}
