package com.Matricula2027.matricula2027.dto;

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
    
    private AutorizadoRetiroDTO autorizadoRetiro;

    @Data
    @NoArgsConstructor
    public static class AlumnaDTO {
        private String rut;
        private String nombres;
        private String apellidoPaterno;
        private String apellidoMaterno;
        private LocalDate fechaNacimiento;
        
        @JsonAlias({"direccionCompleta", "direccion"})
        private String direccion; 
        
        private String comuna;
        private String viveCon;
    }

    @Data
    public static class FamiliarDTO {
        private String rut;
        private String nombres;
        private String apellidos;
        private String parentesco;

        @JsonAlias({"direccionCompleta", "direccion"})
        private String direccion;

        private String comuna;

        @JsonAlias({"telefono1", "telefono"})
        private String telefono;

        @JsonAlias({"correoElectronico", "correo"})
        private String correo;

        private boolean esMismoQueApoderado; 
    }

    @Data
    public static class FichaMedicaDTO {
        private Boolean esAlergica;
        private String detalleAlergias;
        private Boolean tomaMedicamentos;
        private String detalleMedicamentos;
        private String condicionMedicaAdicional;
    }

    @Data
    public static class AutorizadoRetiroDTO {
        private String nombreCompleto;
        private String rut;
        private String parentesco; 
        private String telefono;   
    }
}