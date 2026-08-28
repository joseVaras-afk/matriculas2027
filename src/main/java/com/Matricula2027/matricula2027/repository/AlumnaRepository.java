package com.Matricula2027.matricula2027.repository;

import com.Matricula2027.matricula2027.entity.Alumna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlumnaRepository extends JpaRepository<Alumna, Long> {
    // Spring Boot creará la consulta SQL automáticamente para buscar si existe el RUT
    boolean existsByRut(String rut);
}