package com.gestorpyme.security;

import com.gestorpyme.mock.MockDataStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    private final boolean securityEnabled;

    public CurrentUserProvider(@Value("${app.security.enabled:false}") boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    public String getCurrentUserEmail() {
        if (!securityEnabled) {
            return "admin@gestorpyme.local";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new org.springframework.security.authentication.AuthenticationCredentialsNotFoundException(
                    "Usuário não autenticado");
        }
        return auth.getName();
    }

    public String getCreatedByLabel() {
        if (!securityEnabled) {
            return MockDataStore.DEFAULT_USER;
        }
        return getCurrentUserEmail();
    }
}
