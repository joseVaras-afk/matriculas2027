package com.Matricula2027.matricula2027.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "alumnas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Alumna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 12)
    private String rut;

    @Column(nullable = false)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = false)
    private String apellidoMaterno;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    private String nacionalidad;

    private String direccion;
    
    private String comuna;
    
    @Column(name = "vive_con")
    private String viveCon;
    
    @Column(name = "necesidad_educativa_especial")
    private String necesidadEducativaEspecial;
}