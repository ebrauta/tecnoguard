package com.github.tecnoguard.security;

import com.github.tecnoguard.domain.enums.UserRole;
import com.github.tecnoguard.domain.models.User;
import com.github.tecnoguard.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthenticationManagerTest {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepo;

    @BeforeEach
    void setUp() {
        // Limpa e recria o usuário admin no banco antes de cada teste
        userRepo.deleteAll();

        User user = new User();
        user.setUsername("admin");
        user.setPassword(encoder.encode("1234"));
        user.setRole(UserRole.ADMIN);

        userRepo.save(user);
    }

    @Test
    @DisplayName("Deve autenticar usuário válido com senha correta")
    void shouldAuthenticateValidUser() {
        Authentication request = new UsernamePasswordAuthenticationToken("admin", "1234");

        Authentication result = authManager.authenticate(request);

        assertTrue(result.isAuthenticated(), "Usuário deve ser autenticado");
        assertEquals("admin", result.getName());
    }

    @Test
    @DisplayName("Não deve autenticar com senha incorreta")
    void shouldFailWithWrongPassword() {
        Authentication request = new UsernamePasswordAuthenticationToken("admin", "senhaErrada");

        assertThrows(Exception.class, () -> authManager.authenticate(request),
                "Deve lançar exceção para senha incorreta");
    }

    @Test
    @DisplayName("Não deve autenticar usuário inexistente")
    void shouldFailWithUnknownUser() {
        Authentication request = new UsernamePasswordAuthenticationToken("invalido", "1234");

        assertThrows(Exception.class, () -> authManager.authenticate(request),
                "Deve lançar exceção para usuário inexistente");
    }
}