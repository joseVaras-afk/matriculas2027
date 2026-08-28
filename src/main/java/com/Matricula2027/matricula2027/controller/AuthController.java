package com.Matricula2027.matricula2027.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String usuario = credenciales.get("usuario");
        String password = credenciales.get("password");

        // Credenciales simples de prueba (puedes migrarlas a base de datos después)
        if ("admin".equals(usuario) && "123".equals(password)) {
            return ResponseEntity.ok(Map.of(
                "mensaje", "Login exitoso",
                "nombreFuncionario", "Secretaría General"
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Usuario o contraseña incorrectos"));
    }
}