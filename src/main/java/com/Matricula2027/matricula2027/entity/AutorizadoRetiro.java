package com.Matricula2027.matricula2027.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "autorizados_retiro")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutorizadoRetiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(length = 12)
    private String rut;

    @Column(name = "parentesco_furgon")
    private String parentescoFurgon;

    @Column(name = "telefono")
    private String telefono;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;
}