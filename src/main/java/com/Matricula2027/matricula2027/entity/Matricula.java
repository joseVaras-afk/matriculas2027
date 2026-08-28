package com.Matricula2027.matricula2027.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "matriculas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_matricula", unique = true, nullable = false)
    private String numeroMatricula;

    @Column(name = "fecha_hora_registro", nullable = false)
    private LocalDateTime fechaHoraRegistro;

    @Column(name = "curso_actual", nullable = false)
    private String cursoActual;

    @Column(name = "estado_ficha", nullable = false)
    private String estadoFicha; 

    @Column(name = "correo_comprobante")
    private String correoComprobante;

   
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "alumna_id", referencedColumnName = "id")
    private Alumna alumna;

  
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ficha_medica_id", referencedColumnName = "id")
    private FichaMedica fichaMedica;

  
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "apoderado_titular_id")
    private Familiar apoderadoTitular;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "apoderado_suplente_id")
    private Familiar apoderadoSuplente;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "madre_id")
    private Familiar madre;

  
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "padre_id")
    private Familiar padre;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "autorizado_retiro_id")
    private AutorizadoRetiro autorizadosRetiro;

    @Column(name = "curso_postulacion")
    private String cursoPostulacion;
}