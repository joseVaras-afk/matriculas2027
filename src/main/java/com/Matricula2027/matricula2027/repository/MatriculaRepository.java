package com.Matricula2027.matricula2027.repository;

import com.Matricula2027.matricula2027.entity.Matricula;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    // Para buscar el comprobante por su número único
    Optional<Matricula> findByNumeroMatricula(String numeroMatricula);

    // Para cuando el colegio quiera ver todas las matrículas de un curso específico
    List<Matricula> findByCursoActual(String cursoActual);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Matricula m SET m.estadoFicha = :nuevoEstado WHERE m.id = :id")
    int actualizarEstadoDirecto(@Param("id") Long id, @Param("nuevoEstado") String nuevoEstado);

    @Query("SELECT DISTINCT m FROM Matricula m " +
            "LEFT JOIN FETCH m.alumna " +
            "LEFT JOIN FETCH m.apoderadoTitular " +
            "LEFT JOIN FETCH m.apoderadoSuplente")
    List<Matricula> findAllConRelaciones();
}