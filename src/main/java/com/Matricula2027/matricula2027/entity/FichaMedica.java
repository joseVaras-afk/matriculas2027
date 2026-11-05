package com.Matricula2027.matricula2027.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ficha_medica")
@Data
@NoArgsConstructor
public class FichaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean esAlergica;
    private String detalleAlergias;
    private Boolean tomaMedicamentos;
    private String detalleMedicamentos;
    private String condicionMedicaAdicional;
    private String enfermedad;
    private String enfermedadCual;
    private String tratamiento;
    private String contramedicacion;
    private String prevsion;
    private String cesfam;
    private String personaEmergencia;
    private String telEmergencia;
    private String parentescoEmergencia;
}