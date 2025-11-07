package com.github.tecnoguard.infrastructure.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tecnoguard.application.dtos.auth.request.LoginDTO;
import com.github.tecnoguard.application.dtos.auth.response.LoginResponseDTO;
import com.github.tecnoguard.application.dtos.workorder.request.AssignWO;
import com.github.tecnoguard.application.dtos.workorder.request.CancelWO;
import com.github.tecnoguard.application.dtos.workorder.request.CompleteWO;
import com.github.tecnoguard.domain.enums.UserRole;
import com.github.tecnoguard.domain.enums.WOStatus;
import com.github.tecnoguard.domain.enums.WOType;
import com.github.tecnoguard.domain.models.User;
import com.github.tecnoguard.domain.models.WorkOrder;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WorkOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private WorkOrder order;
    private AssignWO assignDTO;
    private CompleteWO completeDTO;
    private CancelWO cancelDTO;


    @BeforeEach
    void setUp() throws Exception {
        order = new WorkOrder(
                "Trocar motor",
                "Bomba 3",
                "Cliente X",
                WOType.CORRETIVA);

        assignDTO = new AssignWO("Técnico 1", LocalDate.of(2025, 10, 15));
        completeDTO = new CompleteWO("Serviço concluído com sucesso");
        cancelDTO = new CancelWO("Equipamento já substituído");
    }

    private long createWorkOrder() throws Exception {
        String response = mockMvc.perform(
                        post("/api/workorders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(order))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Trocar motor"))
                .andExpect(jsonPath("$.equipment").value("Bomba 3"))
                .andExpect(jsonPath("$.client").value("Cliente X"))
                .andExpect(jsonPath("$.type").value(WOType.CORRETIVA.toString()))
                .andExpect(jsonPath("$.status").value(WOStatus.OPEN.toString()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return mapper.readTree(response).get("id").asLong();
    }

    @Test
    @DisplayName("Controller - Deve criar uma OS")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldCreateWorkOrder() throws Exception {
        createWorkOrder();
    }


    @Test
    @DisplayName("Controller - Deve listar todas as OS")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldListWorkOrders() throws Exception {
        createWorkOrder();
        mockMvc.perform(get("/api/workorders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content[0].description").value("Trocar motor"))
                .andExpect(jsonPath("$.content[0].status").value("OPEN"));

    }

    @Test
    @DisplayName("Controller - Deve buscar OS por ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldGetWorkOrderById() throws Exception {
        long id = createWorkOrder();

        mockMvc.perform(get("/api/workorders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.description").value("Trocar motor"));
    }

    @Test
    @DisplayName("Controller - Deve agendar uma OS (OPEN → SCHEDULED)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAssignWorkOrder() throws Exception {
        long id = createWorkOrder();

        mockMvc.perform(
                patch("/api/workorders/assign/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(assignDTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedTechnician").value("Técnico 1"))
                .andExpect(jsonPath("$.scheduledDate").value("2025-10-15"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }


    @Test
    @DisplayName("Controller - Deve iniciar execução (SCHEDULED → IN_PROGRESS)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldStartWorkOrder() throws Exception {
        long id = createWorkOrder();

        // primeiro agenda
        mockMvc.perform(patch("/api/workorders/assign/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(assignDTO))
                        )
                .andExpect(status().isOk());

        // agora inicia
        mockMvc.perform(patch("/api/workorders/start/{id}", id)
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("Controller - Deve completar a OS (IN_PROGRESS → COMPLETED)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldCompleteWorkOrder() throws Exception {
        long id = createWorkOrder();

        // agenda e inicia
        mockMvc.perform(
                        patch("/api/workorders/assign/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(assignDTO))
                )
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/workorders/start/{id}", id))
                .andExpect(status().isOk());

        // completa
        mockMvc.perform(patch("/api/workorders/complete/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(completeDTO))
                        /*.with(auth())*/)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Controller - Deve cancelar uma OS (IN_PROGRESS → CANCELLED)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldCancelWorkOrder() throws Exception {
        long id = createWorkOrder();

        // agenda e inicia
        mockMvc.perform(patch("/api/workorders/assign/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(assignDTO))
                        )
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/workorders/start/{id}", id)
                        )
                .andExpect(status().isOk());

        // cancela
        mockMvc.perform(patch("/api/workorders/cancel/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(cancelDTO))
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.cancelReason").value("Equipamento já substituído"));
    }

    @Test
    @DisplayName("Controller - Deve retornar 401 se não autenticado")
    void shouldRejectUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/workorders"))
                .andExpect(status().isForbidden());
    }
}