package com.Matricula2027.matricula2027.dto;

import java.time.LocalDateTime;

import lombok.Data;

public record MatriculaListadoDTO(
        Long id,
        String numeroMatricula,
        String nombreCompletoAlumna,
        String cursoPostulacion,
        String estadoFicha) {
}