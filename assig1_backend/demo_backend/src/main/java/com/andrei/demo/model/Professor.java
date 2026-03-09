package com.andrei.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "professor")
@EqualsAndHashCode(callSuper = true)
public class Professor extends Person {
    @Column(name = "department")
    private String department;

    @Column(name = "academic_rank")
    private String academicRank;
}
