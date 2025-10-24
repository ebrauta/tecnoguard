package com.github.tecnoguard.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tecnoguard.domain.enums.WOStatus;
import com.github.tecnoguard.domain.enums.WOType;
import com.github.tecnoguard.domain.model.WorkOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @BeforeEach
    void setUp() {
        order = new WorkOrder(
                "Trocar motor",
                "Bomba 3",
                "Cliente X",
                WOType.CORRETIVA);
    }

    @Test
    @DisplayName("Controller - Deve criar uma OS")
    void shouldCreateWorkOrder() throws Exception {
        mockMvc.perform(
                        post("/api/workorders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(order))
                                .with(httpBasic("admin", "1234"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Trocar motor"))
                .andExpect(jsonPath("$.equipment").value("Bomba 3"))
                .andExpect(jsonPath("$.client").value("Cliente X"))
                .andExpect(jsonPath("$.type").value(WOType.CORRETIVA.toString()))
                .andExpect(jsonPath("$.status").value(WOStatus.OPEN.toString()))
                .andExpect(jsonPath("$.workOrderLog").isArray())
                .andExpect(jsonPath("$.workOrderLog").isNotEmpty())
        ;
    }

    @Test
    void list() {
    }

    @Test
    void get() {
    }


    @Test
    void assign() {
    }

    @Test
    void start() {
    }

    @Test
    void complete() {
    }

    @Test
    void cancel() {
    }
}