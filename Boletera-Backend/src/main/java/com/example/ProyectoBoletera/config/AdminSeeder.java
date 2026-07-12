package com.example.ProyectoBoletera.config;

import com.example.ProyectoBoletera.dominio.model.Administrador;
import com.example.ProyectoBoletera.dominio.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    // Crea el administrador inicial al arrancar
    @Bean
    CommandLineRunner initAdmin(AdministradorRepository administradorRepository,
                                PasswordEncoder passwordEncoder,
                                @Value("${app.admin.email:}") String email,
                                @Value("${app.admin.password:}") String password) {
        return args -> {
            if (administradorRepository.count() == 0 && !email.isBlank() && !password.isBlank()) {
                administradorRepository.save(new Administrador(
                        "Administrador", email, passwordEncoder.encode(password), null));
            }
        };
    }
}
