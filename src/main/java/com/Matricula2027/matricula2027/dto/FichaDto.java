package com.Matricula2027.matricula2027.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class FichaDto {

    private String curso;
    private String correoComprobante;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String fechaHoraRegistro;
    private String numeroMatricula;
    private String curso2027;

    // Alumna
    private String nombreAlumna;
    private String apellidoPAlumna;
    private String apellidoMAlumna;
    private String rutAlumna;
    private String alumnoFechaN;
    private String alumnaDireccion;
    private String aluComuna;
    private String viveCon;
    private String aluNomCom;

    private String alumnaVilla;
    private String alumnaNacionalidad;
    private String etniaSiNo;
    private String etnia;
    private String procedencia;
    private String nivelesRepetidos;

    // Apoderado Titular
    private String apoNombre;
    private String apoApellidos;
    private String apoRut;
    private String apoParentesco;
    private String apoTelefono;
    private String apoCorreo;
    private String apoDireccion;
    private String apoComuna;
    private String apoNomCom;

    private Integer edadApo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String apoFechaNac;
    private String generoApo;
    private String apoVilla;
    private String nivEstudioApo;
    private String ocupacionApo;
    private String jornadaApo;
    private String telLabApo;
    private String direcLabApo;
    private String apoEstCivil;
    private String apoNivEst;
    private String apoOcupacion;
    private String apoJornada;
    private String apoTelLab;
    private String apoDirecLab;

    // Apoderado Suplente
    private String apo2Nombre;
    private String apo2Apellidos;
    private String apo2Rut;
    private String apo2Parentesco;
    private String apo2Telefono;
    private String apo2Correo;
    private String apo2Dire;
    private String apo2Comuna;

    private Integer edadApo2;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String fechaNacApo2;
    private String generoApo2;
    private String apo2EstCivil;
    private String apo2NivEst;
    private String apo2Ocupacion;
    private String apo2Jornada;
    private String apo2TelLab;
    private String apo2DirecLab;
    private String apo2Villa;

    // Retira 1
    private String nombreRet1;
    private String rutRetira1;
    private String parenRet1;
    private String telRet1;

    // Retira 2
    private String nombreRet2;
    private String rutRetira2;
    private String parenRet2;
    private String telRet2;

    // Retira 3
    private String nombreRet3;
    private String rutRetira3;
    private String parenRet3;
    private String telRet3;

    private String retSola;
    private String condCont;
    private String condRun;
    private String condTel;

    // Datos Familiares
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

    // Ficha médica
    private String esAlergica;
    private String detalleAlergia;
    private String tomaMed;
    private String detalleMed;
    private String condicionMed;
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