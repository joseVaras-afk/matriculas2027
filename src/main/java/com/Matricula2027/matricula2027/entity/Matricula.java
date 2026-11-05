package com.Matricula2027.matricula2027.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "matricula")
@Data
@NoArgsConstructor
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroMatricula;

    private String cursoActual;
    private String cursoPostulacion;
    private LocalDateTime fechaHoraRegistro;
    private String estadoFicha;
    private String correoComprobante;

    private String retSola;
    private String condCont;
    private String condRun;
    private String condTel;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "alumna_id")
    private Alumna alumna;

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
    @JoinColumn(name = "autorizado1_id")
    private AutorizadoRetiro autorizado1;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "autorizado2_id")
    private AutorizadoRetiro autorizado2;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "autorizado3_id")
    private AutorizadoRetiro autorizado3;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "datos_familiares_id")
    private DatosFamiliares datosFamiliares;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ficha_medica_id")
    private FichaMedica fichaMedica;
}