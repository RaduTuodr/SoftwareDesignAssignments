package com.andrei.demo.repository;

import com.andrei.demo.model.Code;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CodeRepository extends JpaRepository<Code, UUID> {

    Optional<Code> findTopByPersonUuidOrderByExpirationDateDesc(UUID personId);
}
