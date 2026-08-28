package com.Matricula2027.matricula2027.repository;

import com.Matricula2027.matricula2027.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Lo usaremos más adelante para el Login
    Optional<Usuario> findByUsername(String username);
}