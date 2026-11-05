package com.Matricula2027.matricula2027.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "autorizado_retiro")
@Data
@NoArgsConstructor
public class AutorizadoRetiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rut;
    private String nombreCompleto;
    private String parentescoFurgon;
    private String telefono;
}