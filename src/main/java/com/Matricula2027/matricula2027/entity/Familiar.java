package com.Matricula2027.matricula2027.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "familiar")
@Data
@NoArgsConstructor
public class Familiar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String rut;

    private String nombres;
    private String apellidos;
    private String parentesco;
    private String direccion;
    private String comuna;
    private String telefono;
    private String correo;

    private Integer edad;
    private String apoFechaNac;
    private String apoGenero;
    private String apoVilla;
    private String nivEstudioApo;
    private String ocupacionApo;
    private String jornadaApo;
    private String telLabApo;
    private String direcLabApo;
    private String apoEstCivil;
}