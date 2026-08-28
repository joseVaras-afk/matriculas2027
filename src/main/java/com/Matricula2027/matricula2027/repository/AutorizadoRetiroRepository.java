package com.Matricula2027.matricula2027.repository;

import com.Matricula2027.matricula2027.entity.AutorizadoRetiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorizadoRetiroRepository extends JpaRepository<AutorizadoRetiro, Long> {
}