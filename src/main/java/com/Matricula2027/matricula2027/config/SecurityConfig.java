package com.Matricula2027.matricula2027.config;

import org.springframework.boot.security.autoconfigure.web.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
    // Recursos estáticos y rutas públicas
    .requestMatchers(
        "/", 
        "/index", 
        "/index.html", 
        "/css/**", 
        "/js/**", 
        "/images/**", 
        "/favicon.ico",
        "/webjars/**",
        "/matricula/**"
    ).permitAll()
    
    // Rutas protegidas
    .requestMatchers("/admin/**").authenticated()
    .anyRequest().authenticated()
)
            
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}