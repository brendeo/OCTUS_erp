package com.gestorpyme.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;

@Configuration
@Profile("prod")
public class ProdConfig {

    @Value("${spring.datasource.url:}")
    private String databaseUrl;

    @Value("${spring.datasource.username:}")
    private String databaseUser;

    @Value("${spring.datasource.password:}")
    private String databasePassword;

    @Value("${app.jwt.secret:}")
    private String jwtSecret;

    @PostConstruct
    void validateProductionConfig() {
        if (databaseUrl == null || databaseUrl.isBlank()) {
            throw new IllegalStateException("DATABASE_URL é obrigatório no perfil prod");
        }
        if (databaseUser == null || databaseUser.isBlank()) {
            throw new IllegalStateException("DATABASE_USER é obrigatório no perfil prod");
        }
        if (databasePassword == null || databasePassword.isBlank()) {
            throw new IllegalStateException("DATABASE_PASSWORD é obrigatório no perfil prod");
        }
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET é obrigatório no perfil prod");
        }
        if (AppConstants.DEFAULT_JWT_SECRET.equals(jwtSecret)) {
            throw new IllegalStateException("JWT_SECRET não pode usar o valor padrão de desenvolvimento em produção");
        }
        if (jwtSecret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET deve ter pelo menos 32 caracteres");
        }
    }
}
