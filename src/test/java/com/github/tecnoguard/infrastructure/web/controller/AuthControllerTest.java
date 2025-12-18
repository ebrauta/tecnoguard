package com.github.tecnoguard.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tecnoguard.application.dtos.auth.request.LoginDTO;
import com.github.tecnoguard.application.dtos.auth.response.LoginResponseDTO;
import com.github.tecnoguard.application.dtos.user.request.CreateUserDTO;
import com.github.tecnoguard.domain.enums.UserRole;
import com.github.tecnoguard.domain.models.User;
import com.github.tecnoguard.infrastructure.persistence.UserRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    private User user;
    private LoginDTO loginDTO;


    @BeforeEach
    void setup() {
        user = new User();
        user.setUsername("joao");
        user.setPassword(encoder.encode("1234"));
        user.setUserRole(UserRole.TECHNICIAN);
        user.setEmail("joao@mail.com");

        loginDTO = new LoginDTO("joao", "1234");

        userRepo.deleteAll(); // limpa o banco antes de cada teste
    }

    @Test
    @DisplayName("AuthController - Deve autenticar usuário existente com senha correta")
    void shouldLoginSuccessfully() throws Exception {
        // cria usuário manualmente no repositório
        userRepo.save(user);


        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("joao"))
                .andExpect(jsonPath("$.message").value("Autenticação realizada com sucesso"));
    }

    @Test
    @DisplayName("AuthController- Não deve autenticar com senha incorreta")
    void shouldFailLoginWithWrongPassword() throws Exception {
        userRepo.save(user);

        LoginDTO dto = new LoginDTO("joao", "senhaErrada");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Usuário ou Senha inválidos"));
    }

    @Test
    @DisplayName("AuthController - Não deve autenticar usuário inexistente")
    void shouldFailLoginWithUnknownUser() throws Exception {
        LoginDTO dto = new LoginDTO("inexistente", "1234");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Usuário ou Senha inválidos"));
    }

    @Test
    @DisplayName("AuthController - Deve retornar dados do usuário autenticado")
    void shouldReturnLoggedUserInfo() throws Exception {
        userRepo.save(user);
        var response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDTO)))
                .andReturn().getResponse().getContentAsString();

        String token = mapper.readValue(response, LoginResponseDTO.class).token();

        mockMvc.perform(get("/api/auth/whoami")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .jwt()
                                .jwt(jwt -> jwt
                                        .tokenValue(token)
                                        .claim("sub", "joao")
                                        .claim("scope", "TECHNICIAN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("joao"))
                .andExpect(jsonPath("$.role").value(CoreMatchers.containsString("TECHNICIAN")));
    }

    @Test
    @DisplayName("AuthController - Deve negar acesso a /whoami se não autenticado")
    void shouldRejectUnauthorizedAccessToMe() throws Exception {
        mockMvc.perform(get("/api/auth/whoami"))
                .andExpect(status().isUnauthorized());
    }
}
