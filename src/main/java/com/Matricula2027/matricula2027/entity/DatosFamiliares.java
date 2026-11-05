package com.Matricula2027.matricula2027.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "datos_familiares")
@Data
@NoArgsConstructor
public class DatosFamiliares {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer nIntegrantes;
    private Integer nHermanos;
    private String tieneHermano;
    private String nomHer1;
    private String nomHer2;
    private String cursoHer1;
    private String cursoHer2;
    private String redApoyo;
    private String propia;
    private String arrendada;
    private String cedida;
    private String allegado;
}