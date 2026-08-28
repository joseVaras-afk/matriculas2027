package com.Matricula2027.matricula2027.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fichas_medicas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FichaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "es_alergica", nullable = false)
    private Boolean esAlergica;

    @Column(name = "detalle_alergias", columnDefinition = "TEXT")
    private String detalleAlergias;

    @Column(name = "toma_medicamentos", nullable = false)
    private Boolean tomaMedicamentos;

    @Column(name = "detalle_medicamentos", columnDefinition = "TEXT")
    private String detalleMedicamentos;

    @Column(name = "condicion_medica_adicional", columnDefinition = "TEXT")
    private String condicionMedicaAdicional;
}