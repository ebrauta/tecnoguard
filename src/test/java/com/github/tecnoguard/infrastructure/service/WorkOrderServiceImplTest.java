package com.github.tecnoguard.infrastructure.service;

import com.github.tecnoguard.domain.enums.WOType;
import com.github.tecnoguard.domain.model.WorkOrder;
import com.github.tecnoguard.domain.service.IWorkService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WorkOrderServiceImplTest {

    @Autowired
    private IWorkService service;

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
    @DisplayName("Service - Deve criar uma OS")
    void shouldCreateWorkOrder() {
        WorkOrder w = service.create(order);

        Assertions.assertEquals("Trocar motor", w.getDescription());
        Assertions.assertEquals("Bomba 3", w.getEquipment());
        Assertions.assertEquals("Cliente X", w.getClient());
        Assertions.assertEquals("CORRETIVA", w.getType().toString());
        Assertions.assertEquals("OPEN", w.getStatus().toString());
        Assertions.assertEquals(1, w.getWorkOrderLog().size());
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

    @Test
    void list() {
    }

    @Test
    void findById() {
    }
}