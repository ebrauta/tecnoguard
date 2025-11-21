package com.github.tecnoguard.infrastructure.service;

import com.github.tecnoguard.core.exceptions.BusinessException;
import com.github.tecnoguard.core.exceptions.NotFoundException;
import com.github.tecnoguard.domain.enums.WOPriority;
import com.github.tecnoguard.domain.enums.WOType;
import com.github.tecnoguard.domain.models.WorkOrder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.IntStream;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class WorkOrderServiceImplTest {

    @Autowired
    private WorkOrderOrderServiceImpl service;

    private WorkOrder order;
    private WorkOrder order2;

    @BeforeEach
    void setUp() {
        order = new WorkOrder();
        order.setDescription("Trocar motor");
        order.setEquipment("Bomba 3");
        order.setClient("Cliente X");
        order.setType(WOType.CORRECTIVE);
        order.setPriority(WOPriority.MEDIUM);
        order2 = new WorkOrder();
        order2.setDescription("Trocar eixo");
        order2.setEquipment("Bomba 3");
        order2.setClient("Cliente X");
        order2.setType(WOType.CORRECTIVE);
        order2.setPriority(WOPriority.MEDIUM);
    }

    @Test
    @DisplayName("WorkOrderService - Deve criar uma OS")
    void shouldCreateWorkOrder() {
        WorkOrder w = service.create(order);

        Assertions.assertNotNull(w.getId());
        Assertions.assertEquals("Trocar motor", w.getDescription());
        Assertions.assertEquals("Bomba 3", w.getEquipment());
        Assertions.assertEquals("Cliente X", w.getClient());
        Assertions.assertEquals("CORRECTIVE", w.getType().toString());
        Assertions.assertEquals("OPEN", w.getStatus().toString());

    }

    @Test
    @DisplayName("WorkOrderService - Deve agendar uma OS")
    void shouldAssignWorkOrder() {
        WorkOrder created = service.create(order);
        WorkOrder assigned = service.assign(created.getId(), "Técnico", LocalDate.now());

        Assertions.assertEquals("Técnico", assigned.getAssignedTechnician());
        Assertions.assertEquals(LocalDate.now(), assigned.getScheduledDate());
        Assertions.assertEquals("SCHEDULED", assigned.getStatus().toString());
    }

     @Test
    @DisplayName("WorkOrderService - Deve iniciar uma OS")
    void shouldStartWorkOrder() {
        WorkOrder created = service.create(order);
        WorkOrder assigned = service.assign(created.getId(), "Técnico", LocalDate.now());
        WorkOrder started = service.start(assigned.getId());

        Assertions.assertEquals("IN_PROGRESS", started.getStatus().toString());
    }

    @Test
    @DisplayName("WorkOrderService - Deve completar uma OS")
    void shouldCompleteWorkOrder() {
        WorkOrder created = service.create(order);
        WorkOrder assigned = service.assign(created.getId(), "Técnico", LocalDate.now());
        WorkOrder started = service.start(assigned.getId());
        WorkOrder completed = service.complete(started.getId(), "Solucionada");

        Assertions.assertEquals("COMPLETED", completed.getStatus().toString());
    }

    @Test
    @DisplayName("WorkOrderService - Deve cancelar uma OS")
    void shouldCancelWorkOrder() {
        WorkOrder created = service.create(order);
        WorkOrder assigned = service.assign(created.getId(), "Técnico", LocalDate.now());
        WorkOrder started = service.start(assigned.getId());
        WorkOrder canceled = service.cancel(started.getId(), "Cancelada");

        Assertions.assertEquals("CANCELLED", canceled.getStatus().toString());
        Assertions.assertEquals("Cancelada", canceled.getCancelReason());
    }

    @Test
    @DisplayName("WorkOrderService - Deve listar OS com paginação")
    void shouldListPaginatedWorkOrders() {
        service.create(order);
        service.create(order2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("description").ascending());

        Page<WorkOrder> result = service.list(pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(2, result.getContent().size());
    }

    @Test
    @DisplayName("WorkOrderService - Deve buscar OS por ID")
    void shouldFindWorkOrderById() {
        WorkOrder created = service.create(order);

        WorkOrder found = service.findById(created.getId());

        Assertions.assertNotNull(found);
        Assertions.assertEquals(created.getId(), found.getId());
    }

    @Test
    @DisplayName("WorkOrderService - Deve respeitar limite de página")
    void shouldReturnLimitedPage() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            var orderTest = new WorkOrder();
            orderTest.setDescription("OS " + i);
            orderTest.setEquipment("Equipamento " + i);
            orderTest.setClient("Cliente " + i);
            orderTest.setType(WOType.CORRECTIVE);
            orderTest.setPriority(WOPriority.MEDIUM);
            service.create(orderTest);
        });

        Pageable pageable = PageRequest.of(0, 2);
        Page<WorkOrder> page = service.list(pageable);

        System.out.println(page);

        Assertions.assertEquals(2, page.getContent().size());
        Assertions.assertTrue(page.getTotalPages() >= 3);
    }

    @Test
    @DisplayName("WorkOrderService - Deve lançar exceção ao buscar ID inexistente")
    void shouldThrowWhenWorkOrderNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(999L));
    }

    @Test
    @DisplayName("WorkOrderService - Não deve agendar uma Os com data anterior a hoje")
    void shouldNotAssignWorkOrderWithDateBeforeToday() {
        WorkOrder created = service.create(order);

        Assertions.assertThrows(BusinessException.class, () -> service.assign(created.getId(), "Técnico", LocalDate.of(2025, 11, 10)));;
    }


}