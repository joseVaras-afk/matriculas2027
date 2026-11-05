package com.Matricula2027.matricula2027.dto;

import com.Matricula2027.matricula2027.entity.AutorizadoRetiro;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MatriculaRequestDTO {

    private String cursoActual;
    private String correoComprobante;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String fechaHoraRegistro;
    private String numeroMatricula;
    private String cursoPostulacion;

    private AlumnaDTO alumna;
    private FichaMedicaDTO fichaMedica;
    private FamiliarDTO apoderadoTitular;
    private FamiliarDTO apoderadoSuplente;
    private FamiliarDTO madre;
    private FamiliarDTO padre;

    private DatosFamiliaresDTO datosFamiliares;

    private AutorizadoRetiroDTO autorizado1;
    private AutorizadoRetiroDTO autorizado2;
    private AutorizadoRetiroDTO autorizado3;

    private String retSola;
    private String condContratado;
    private String condRun;
    private String condTel;

    @Data
    @NoArgsConstructor
    public static class AlumnaDTO {
        private String rut;
        private String nombres;
        private String apellidoPaterno;
        private String apellidoMaterno;
        private LocalDate fechaNacimiento;

        @JsonAlias({ "direccionCompleta", "direccion" })
        private String direccion;

        private String comuna;
        private String viveCon;
        private String alumnaVilla;
        private String alumnaNacionalidad;
        private Boolean etniaSiNo;
        private String etnia;
        private String procedencia;
        private String nivelesRepetidos;

    }

    @Data
    public static class FamiliarDTO {
        private String rut;
        private String nombres;
        private String apellidos;
        private String parentesco;

        @JsonAlias({ "direccionCompleta", "direccion" })
        private String direccion;

        private String comuna;

        @JsonAlias({ "telefono1", "telefono" })
        private String telefono;

        @JsonAlias({ "correoElectronico", "correo" })
        private String correo;

        private boolean esMismoQueApoderado;
        private Integer edad;
        private String generoApo;
        private String apoVilla;
        private String nivEstudioApo;
        private String apoFechaNac;
        private String ocupacionApo;
        private String jornadaApo;
        private String telLabApo;
        private String direcLabApo;
        private String apoEstCivil;
    }

    @Data
    public static class FichaMedicaDTO {
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

    @Data
    public static class AutorizadoRetiroDTO {
        private String nombreCompleto;
        private String rut;
        private String parentesco;
        private String telefono;

    }

    @Data
    public static class DatosFamiliaresDTO {
        private String nIntegrantes;
        private String nHermanos;
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
}