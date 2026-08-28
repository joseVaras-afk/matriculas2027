package com.Matricula2027.matricula2027.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "familiares")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Familiar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 12, name = "rut")
    private String rut;

    @Column(nullable = false, name = "nombres")
    private String nombres;

    @Column(nullable = false, name = "apellidos")
    private String apellidos;

    @Column(name = "parentesco")
    private String parentesco;
  
    @Column(name = "direccion_completa")
    private String direccion;
    
    @Column(name = "comuna")
    private String comuna;
    
    @Column(name = "telefono1")
    private String telefono;
    
    @Column(name = "correo_electronico")
    private String correo;
}