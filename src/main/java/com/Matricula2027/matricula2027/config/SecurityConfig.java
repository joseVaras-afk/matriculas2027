package com.Matricula2027.matricula2027.config;

import org.springframework.boot.security.autoconfigure.web.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;

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
        "/assets/**", 
        "/assets/css/**", 
        "/assets/js/**",
        "/webjars/**",
        "/pre-matricula.html",
        "/pre-matricula",
        "/login",
        "/login.html"
    ).permitAll()
    
    // Rutas protegidas
    .requestMatchers(
        "/admin.html",
        "/admin",
        "/comprobante.html",
        "/comprobante",
        "/ficha-impresion.html",
        "/ficha-impresion",
        "/editar-matricula.html",
        "/editar-matricula"
    ).authenticated()
    .anyRequest().authenticated()
)
            
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin.html", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
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
