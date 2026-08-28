package com.Matricula2027.matricula2027.controller;

import com.Matricula2027.matricula2027.dto.MatriculaRequestDTO;
import com.Matricula2027.matricula2027.entity.Matricula;
import com.Matricula2027.matricula2027.service.EmailService;
import com.Matricula2027.matricula2027.service.MatriculaService;

import org.springframework.http.ContentDisposition;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matriculas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<?> registrarMatricula(@RequestBody MatriculaRequestDTO dto) {
        try {
            Matricula matriculaGuardada = matriculaService.registrarMatricula(dto);

            String correoDestino = dto.getCorreoComprobante();
            if (correoDestino == null || correoDestino.isEmpty()) {
                if (dto.getApoderadoTitular() != null) {
                    correoDestino = dto.getApoderadoTitular().getCorreo();
                }
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String fechaFormateada = matriculaGuardada.getFechaHoraRegistro().format(formatter);

            try {
                emailService.enviarComprobante(
                    correoDestino, 
                    matriculaGuardada.getNumeroMatricula(), 
                    fechaFormateada
                );
            } catch (Exception e) {
                System.err.println("Error al enviar el correo automático: " + e.getMessage());
            }

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("numeroMatricula", matriculaGuardada.getNumeroMatricula());
            respuesta.put("fechaRegistro", fechaFormateada);
            respuesta.put("correoComprobante", correoDestino);

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", e.getMessage()));

        } catch (DataIntegrityViolationException e) {
            String rut = (dto.getAlumna() != null) ? dto.getAlumna().getRut() : "";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "La alumna con el RUT " + rut + " ya se encuentra matriculada."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error interno al procesar la matrícula: " + e.getMessage()));
        }
    }

    @PostMapping("/enviar-comprobante")
    public ResponseEntity<?> reEnviarComprobante(@RequestBody Map<String, String> request) {
        String numeroMatricula = request.get("numeroMatricula");
        String correo = request.get("correo");
        String fechaRegistro = request.get("fechaRegistro");

        try {
            emailService.enviarComprobante(correo, numeroMatricula, fechaRegistro);
            return ResponseEntity.ok(Map.of("mensaje", "Correo enviado con éxito."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error al enviar el correo: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Matricula>> listarMatriculas(@RequestParam(required = false) String curso) {
        if (curso != null && !curso.isEmpty()) {
            return ResponseEntity.ok(matriculaService.obtenerPorCurso(curso));
        }
        return ResponseEntity.ok(matriculaService.obtenerTodas());
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        try {
            Matricula matriculaActualizada = matriculaService.actualizarEstado(id, nuevoEstado);
            return ResponseEntity.ok(matriculaActualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "Error al actualizar el estado: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/word")
    public ResponseEntity<byte[]> descargarFichaWord(@PathVariable Long id) {
        try {
            Matricula m = matriculaService.obtenerPorId(id);
            byte[] docxBytes = matriculaService.generarFichaDocx(m);
            String nomArchivo = m.getAlumna().getApellidoPaterno()+" "+ m.getAlumna().getApellidoMaterno()+" "+m.getAlumna().getNombres()+" "+m.getCursoPostulacion();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            headers.setContentDispositionFormData("attachment", nomArchivo+ ".docx");

            return ResponseEntity.ok().headers(headers).body(docxBytes);
        } catch (Exception e) {
            System.err.println("Error al descargar documento Word para ID " + id + ": " + e.getMessage());
            e.printStackTrace(); 
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/pdf")
public ResponseEntity<byte[]> obtenerFichaPdf(@PathVariable Long id) {
    Matricula m = matriculaService.obtenerPorId(id);
    byte[] pdfBytes = matriculaService.generarFichaPdf(m);
    String nomArchivo = m.getAlumna().getApellidoPaterno()+" "+ m.getAlumna().getApellidoMaterno()+" "+m.getAlumna().getNombres()+" "+m.getCursoPostulacion();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDisposition(ContentDisposition.inline()
            .filename(nomArchivo+ ".pdf")
            .build());

    return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
}

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Matricula matricula = matriculaService.obtenerPorId(id);
            return ResponseEntity.ok(matricula);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMatricula(@PathVariable Long id, @RequestBody MatriculaRequestDTO dto) {
        try {
            Matricula matriculaActualizada = matriculaService.actualizarMatricula(id, dto);
            return ResponseEntity.ok(Map.of("mensaje", "Ficha actualizada correctamente", "id", matriculaActualizada.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", "Error al actualizar la ficha: " + e.getMessage()));
        }
    }
}