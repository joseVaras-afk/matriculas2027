package com.Matricula2027.matricula2027.config;

import org.springframework.boot.security.autoconfigure.web.reactive.PathRequest; 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Ajusta según tu frontend/REST API
            .authorizeHttpRequests(auth -> auth
                // 1. RUTAS PÚBLICAS: Acceso libre sin login
                .requestMatchers(
                    "/", 
                    "/index", 
                    "/index.html", 
                    "/css/**", 
                    "/js/**", 
                    "/images/**", 
                    "/favicon.ico"
                ).permitAll()
                
                // 2. RUTAS DE FUNCIONARIOS: Requieren inicio de sesión
                .requestMatchers("/admin/**", "/funcionarios/**", "/api/matricula/**").authenticated()
                
                // 3. Cualquier otra petición no especificada requerirá autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // Ruta hacia la vista de login para funcionarios
                .defaultSuccessUrl("/admin/dashboard", true) // Redirección tras login exitoso
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/") // Al cerrar sesión, vuelve al index público
                .permitAll()
            );

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("Admin2027#"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}