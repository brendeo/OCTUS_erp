package com.gestorpyme.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Profile({"dev", "prod", "test"})
@EntityScan(basePackages = "com.gestorpyme.domain.entity")
@EnableJpaRepositories(basePackages = "com.gestorpyme.repository")
public class JpaConfig {
}
