package com.Matricula2027.matricula2027.repository;

import com.Matricula2027.matricula2027.entity.Familiar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamiliarRepository extends JpaRepository<Familiar, Long> {
    // Nos servirá para verificar si un apoderado ya existe en la base de datos por su RUT
    Optional<Familiar> findByRut(String rut);
}