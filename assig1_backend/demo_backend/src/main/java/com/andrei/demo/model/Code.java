package com.andrei.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "code")
public class Code {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "person_uuid", nullable = false)
    private UUID personUuid;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;
}