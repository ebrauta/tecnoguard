package com.github.tecnoguard.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tecnoguard.application.dtos.user.request.ChangePasswordDTO;
import com.github.tecnoguard.application.dtos.user.request.CreateUserDTO;
import com.github.tecnoguard.application.dtos.user.request.UpdateUserDTO;
import com.github.tecnoguard.domain.enums.UserRole;
import com.github.tecnoguard.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ObjectMapper mapper;

    private CreateUserDTO createDTO;
    private UpdateUserDTO updateDTO;
    private ChangePasswordDTO passwordDTO;

    @BeforeEach
    void setUp() {
        createDTO = new CreateUserDTO("User", "user1", "123456", UserRole.TECHNICIAN, "user1@email.com");
        updateDTO = new UpdateUserDTO("User editado", "user1@novo.com", UserRole.OPERATOR);
        passwordDTO = new ChangePasswordDTO("123456", "novaSenha");

        userRepo.deleteAll();
    }

    private long registerUser() throws Exception {
        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDTO))
                        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@email.com"))
                .andExpect(jsonPath("$.userRole").value("TECHNICIAN"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return mapper.readTree(response).get("id").asLong();
    }

    @Test
    @DisplayName("UserController - Deve registrar novo usuário")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldRegisterUser() throws Exception {
        registerUser();
    }

    @Test
    @DisplayName("UserController - Deve listar todos os usuários")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldListUsers() throws Exception {
        registerUser();
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content[0].name").value("User"))
                .andExpect(jsonPath("$.content[0].username").value("user1"));
    }

    @Test
    @DisplayName("UserController - Deve buscar usuário por ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldGetUserById() throws Exception {
        long id = registerUser();
        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    @DisplayName("UserController - Deve atualizar dados do usuário")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldUpdateUser() throws Exception {
        long id = registerUser();
        mockMvc.perform(patch("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDTO))
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User editado"))
                .andExpect(jsonPath("$.email").value("user1@novo.com"))
                .andExpect(jsonPath("$.userRole").value("OPERATOR"));
    }

    @Test
    @DisplayName("UserController - Deve desativar usuário")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldDeactivateUser() throws Exception {
        long id = registerUser();
        mockMvc.perform(delete("/api/users/deactivate/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("UserController - Deve reativar usuário")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldReactivateUser() throws Exception {
        long id = registerUser();
        mockMvc.perform(patch("/api/users/reactivate/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("UserController - Deve alterar senha do usuário")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldChangePassword() throws Exception {
        long id = registerUser();
        mockMvc.perform(patch("/api/users/password/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(passwordDTO))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("UserController - Deve retornar 401 se não autenticado")
    void shouldRejectUnauthenticatedUserAccess() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }
}