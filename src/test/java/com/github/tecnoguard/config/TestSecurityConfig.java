package com.github.tecnoguard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
@Profile("test") // Ativa somente no perfil "test"
public class TestSecurityConfig {

    // Retorna um Optional com o nome de usuário (String) que deve ser usado para auditoria
    @Bean
    public AuditorAware<String> auditorProvider() {
        // Em testes, sempre usamos o mesmo nome de usuário "system_test"
        return () -> Optional.of("system_test");
    }
}