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

    // Apoderado Suplente
    private String apo2Nombre;
    private String apo2Apellidos;
    private String apo2Rut;
    private String apo2Parentesco;
    private String apo2Telefono;
    private String apo2Correo;
    private String apo2Dire;
    private String apo2Comuna;

    // Madre
    private String madreNombres;
    private String madreApellidos;
    private String madreRut;
    private String madreTel;
    private String madreDir;
    private String madreCom;

    // Padre
    private String padreNombres;
    private String padreApellidos;
    private String padreRut;
    private String padreTel;
    private String padreDir;
    private String padreCom;

    // Retira
    private String nombreRet;
    private String rutRetira;
    private String parenRet;
    private String telRet;

    // Ficha médica
    private String esAlergica;
    private String detalleAlergia;
    private String tomaMed;
    private String detalleMed;
    private String condicionMed;
}