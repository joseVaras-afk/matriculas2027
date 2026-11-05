package com.Matricula2027.matricula2027.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "alumna")
@Data
@NoArgsConstructor
public class Alumna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String rut;

    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String comuna;
    private String viveCon;

    private String alumnaVilla;
    private String nacionalidad;
    private String etniaSiNo;
    private String etnia;
    private String procedencia;
    private String nivelesRepetidos;
}