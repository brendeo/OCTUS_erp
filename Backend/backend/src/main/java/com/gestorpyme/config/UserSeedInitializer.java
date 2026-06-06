package com.gestorpyme.config;

import com.gestorpyme.domain.entity.UserEntity;
import com.gestorpyme.domain.enums.UserRole;
import com.gestorpyme.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "prod"})
public class UserSeedInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeedInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedUsers() {
        seedIfMissing("admin@gestorpyme.local", "Administrador", UserRole.GESTOR, "admin123");
        seedIfMissing("operador@gestorpyme.local", "Operador Demo", UserRole.OPERADOR, "operador123");
        seedIfMissing("estoque@gestorpyme.local", "Responsável Estoque", UserRole.ESTOQUE, "estoque123");
        seedIfMissing("contabil@gestorpyme.local", "Apoio Contábil", UserRole.CONTABIL, "contabil123");
    }

    private void seedIfMissing(String email, String nome, UserRole perfil, String senha) {
        if (userRepository.existsByEmail(email)) {
            return;
        }
        UserEntity user = new UserEntity();
        user.setNome(nome);
        user.setEmail(email);
        user.setSenhaHash(passwordEncoder.encode(senha));
        user.setPerfil(perfil);
        userRepository.save(user);
    }
}
