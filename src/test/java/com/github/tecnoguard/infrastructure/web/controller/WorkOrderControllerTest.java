package com.github.tecnoguard.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tecnoguard.application.dtos.workorder.request.*;
import com.github.tecnoguard.core.utils.NoteFormatter;
import com.github.tecnoguard.domain.enums.WOPriority;
import com.github.tecnoguard.domain.enums.WOStatus;
import com.github.tecnoguard.domain.enums.WOType;
import com.github.tecnoguard.domain.models.WorkOrder;
import com.github.tecnoguard.domain.models.WorkOrderNote;
import com.github.tecnoguard.domain.service.IWorkOrderNoteService;
import com.github.tecnoguard.domain.service.IWorkOrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class WorkOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private IWorkOrderService service;
    @Autowired
    private IWorkOrderNoteService noteService;
    @Autowired
    private NoteFormatter formatter;

    private CreateRequest order;
    private AssignRequest assignDTO;
    private CompleteRequest completeDTO;
    private CancelRequest cancelDTO;
    private AddNoteWO noteDTO;

    @BeforeEach
    void setUp() throws Exception {
        order = new CreateRequest(
                "Trocar motor",
                "Bomba 3",
                "Cliente X",
                WOType.CORRECTIVE,
                WOPriority.MEDIUM,
                1.0,
                100.0
        )
        ;

        assignDTO = new AssignRequest("Técnico 1", LocalDateTime.now().plusDays(1));
        completeDTO = new CompleteRequest("Serviço concluído com sucesso", 0.5, 150.0);
        cancelDTO = new CancelRequest("Equipamento já substituído");
        noteDTO = new AddNoteWO("Teste de log via controller");
    }
    private String assignToJson(AssignRequest dto){
        StringBuffer result = new StringBuffer();
        result.append("{\"assignedTechnician\":\"");
        result.append(dto.assignedTechnician());
        result.append("\",\"scheduledDate\":\"");
        result.append(dto.scheduledDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        result.append("\"}");
        return result.toString();
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
                .andExpect(jsonPath("$.type").value(WOType.CORRECTIVE.toString()))
                .andExpect(jsonPath("$.status").value(WOStatus.OPEN.toString()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return mapper.readTree(response).get("id").asLong();
    }

    @Test
    @DisplayName("WorkOrderController - Deve criar uma OS")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldCreateWorkOrder() throws Exception {
        createWorkOrder();
    }

    @Test
    @DisplayName("WorkOrderController - Não deve criar se não tiver papel de ADMIN, PLANNER ou OPERATOR")
    @WithMockUser(username = "teste", roles = {"TECHNICIAN"})
    void create_shouldThrowAccessDeniedException_whenUserTryCreateButDoesNotHaveRequiredRole() throws Exception {
        mockMvc.perform(
                        post("/api/workorders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(order))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("WorkOrderController - Deve listar todas as OS")
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
    @DisplayName("WorkOrderController - Deve buscar OS por ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldGetWorkOrderById() throws Exception {
        long id = createWorkOrder();

        mockMvc.perform(get("/api/workorders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.description").value("Trocar motor"));
    }

    @Test
    @DisplayName("WorkOrderController - Deve agendar uma OS (OPEN → SCHEDULED)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAssignWorkOrder() throws Exception {
        long id = createWorkOrder();

        mockMvc.perform(
                        patch("/api/workorders/assign/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(assignToJson(assignDTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedTechnician").value("Técnico 1"))
                .andExpect(jsonPath("$.scheduledDate").value(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    @DisplayName("WorkOrderController - Não deve agendar se não tiver papel de ADMIN ou PLANNER")
    @WithMockUser(username = "teste", roles = {"OPERATOR"})
    void create_shouldThrowAccessDeniedException_whenUserTryAssignButDoesNotHaveRequiredRole() throws Exception {
        long id = createWorkOrder();
        mockMvc.perform(
                        patch("/api/workorders/assign/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(order))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("WorkOrderController - Deve iniciar execução (SCHEDULED → IN_PROGRESS)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldStartWorkOrder() throws Exception {
        long id = createWorkOrder();

        mockMvc.perform(patch("/api/workorders/assign/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignToJson(assignDTO))
                )
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/workorders/start/{id}", id)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("WorkOrderController - Deve completar a OS (IN_PROGRESS → COMPLETED)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldCompleteWorkOrder() throws Exception {
        long id = createWorkOrder();

        mockMvc.perform(
                        patch("/api/workorders/assign/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(assignToJson(assignDTO))
                )
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/workorders/start/{id}", id))
                .andExpect(status().isOk());


        mockMvc.perform(patch("/api/workorders/complete/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(completeDTO))
                        /*.with(auth())*/)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("WorkOrderController - Deve cancelar uma OS (IN_PROGRESS → CANCELLED)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldCancelWorkOrder() throws Exception {
        long id = createWorkOrder();

        mockMvc.perform(patch("/api/workorders/assign/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignToJson(assignDTO))
                )
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/workorders/start/{id}", id)
                )
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/workorders/cancel/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cancelDTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.cancelReason").value("Equipamento já substituído"));
    }

    @Test
    @DisplayName("WorkOrderController - Deve retornar 401 se não autenticado")
    void shouldRejectUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/workorders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("WorkOrderController - Deve listar notas da OS via GET /log/{id}")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldListLogsForWorkOrder() throws Exception {
        WorkOrder wo = service.findById(createWorkOrder());

        noteService.addNote(wo, "Nota 1", "Maria");
        noteService.addNote(wo, "Nota 2", "João");

        var result = mockMvc.perform(get("/api/workorders/log/{id}", wo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("Nota 1"));
        Assertions.assertTrue(content.contains("Nota 2"));
    }

    @Test
    @DisplayName("WorkOrderController - Deve adicionar uma nota à OS via POST /log/{id}")
    @WithMockUser(username = "eduardo", roles = {"ADMIN"})
    void shouldAddLogToWorkOrder() throws Exception {
        WorkOrder wo = service.findById(createWorkOrder());

        var result = mockMvc.perform(post("/api/workorders/log/{id}", wo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(noteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.author").value("eduardo"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Teste de log")))
                .andReturn();

        List<WorkOrderNote> notes = noteService.listNotes(wo.getId(), null).getContent();
        Assertions.assertEquals(2, notes.size());
        Assertions.assertEquals("eduardo", notes.getFirst().getAuthor());
        Assertions.assertTrue(notes.getFirst().getMessage().contains(formatter.format("Teste de log via controller", "eduardo")));
    }
}