package com.Matricula2027.matricula2027.repository;

import com.Matricula2027.matricula2027.entity.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    // Para buscar el comprobante por su número único
    Optional<Matricula> findByNumeroMatricula(String numeroMatricula);
    
    // Para cuando el colegio quiera ver todas las matrículas de un curso específico
    List<Matricula> findByCursoActual(String cursoActual);
}