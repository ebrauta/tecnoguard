package com.github.tecnoguard.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tecnoguard.application.dtos.user.request.LoginDTO;
import com.github.tecnoguard.application.dtos.user.request.RegisterUserDTO;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
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
    private RegisterUserDTO registerDTO;
    private LoginDTO loginDTO;


    @BeforeEach
    void setup() {
        user = new User();
        user.setUsername("joao");
        user.setPassword(encoder.encode("1234"));
        user.setRole(UserRole.TECHNICIAN);
        user.setEmail("joao@mail.com");

        registerDTO = new RegisterUserDTO("joao", "1234", UserRole.TECHNICIAN, "joao@mail.com");

        loginDTO = new LoginDTO("joao", "1234");

        userRepo.deleteAll(); // limpa o banco antes de cada teste
    }

    @Test
    @DisplayName("Auth - Deve registrar um novo usuário com sucesso")
    void shouldRegisterNewUser() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("joao"))
                .andExpect(jsonPath("$.role").value("TECHNICIAN"));
    }

    @Test
    @DisplayName("Auth - Não deve registrar usuário duplicado")
    void shouldNotRegisterDuplicateUser() throws Exception {
        userRepo.save(user);
        ;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(CoreMatchers.containsString("Usuário já existe")));
    }

    @Test
    @DisplayName("Auth - Deve autenticar usuário existente com senha correta")
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
    @DisplayName("Auth - Não deve autenticar com senha incorreta")
    void shouldFailLoginWithWrongPassword() throws Exception {
        userRepo.save(user);

        LoginDTO dto = new LoginDTO("joao", "senhaErrada");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(CoreMatchers.containsString("Usuário ou senha inválidos")));
    }

    @Test
    @DisplayName("Auth - Não deve autenticar usuário inexistente")
    void shouldFailLoginWithUnknownUser() throws Exception {
        LoginDTO dto = new LoginDTO("inexistente", "1234");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(CoreMatchers.containsString("Usuário ou senha inválidos")));
    }

    @Test
    @DisplayName("Auth - Deve retornar dados do usuário autenticado")
    void shouldReturnLoggedUserInfo() throws Exception {
        userRepo.save(user);

        mockMvc.perform(get("/api/auth/aboutme")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("joao", "1234")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("joao"))
                .andExpect(jsonPath("$.roles").value(CoreMatchers.containsString("TECHNICIAN")));
    }

    @Test
    @DisplayName("Auth - Deve negar acesso a /me se não autenticado")
    void shouldRejectUnauthorizedAccessToMe() throws Exception {
        mockMvc.perform(get("/api/auth/aboutme"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(CoreMatchers.containsString("Não autenticado")));
    }
}
