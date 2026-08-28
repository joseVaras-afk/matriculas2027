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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 1. Liberar automáticamente todas las carpetas estáticas estándar (static, public, resources)
                .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/assets/**", "/favicon.ico").permitAll()

                // 2. Rutas públicas principales y la vista de tu Login programado
                .requestMatchers("/", "/index", "/index.html", "/login").permitAll()

                // 3. Proceso público de pre-matrícula
                .requestMatchers("/matricula/**", "/api/matriculas/registrar").permitAll()

                // 4. Rutas protegidas (Panel administrativo)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login.html")              // Indica la ruta de TU vista personalizada de login
                .loginProcessingUrl("/login")     // Endpoint que procesa el POST del formulario de login
                .defaultSuccessUrl("/admin.html", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/?logout")
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