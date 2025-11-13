package com.github.tecnoguard.infrastructure.service;

import com.github.tecnoguard.domain.enums.WOType;
import com.github.tecnoguard.domain.models.WorkOrder;
import com.github.tecnoguard.domain.models.WorkOrderNote;
import com.github.tecnoguard.infrastructure.persistence.WorkOrderNoteRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class WorkOrderNoteServiceImplTest {

    @Autowired
    private WorkOrderServiceImpl woService;

    @Autowired
    private WorkOrderNoteRepository repo;

    @Autowired
    private WorkOrderNoteServiceImpl service;

    private WorkOrder workOrder;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        WorkOrder created =  new WorkOrder();
        created.setDescription("Trocar motor");
        created.setEquipment("Bomba 3");
        created.setClient("Cliente X");
        created.setType(WOType.CORRECTIVE);
        workOrder = woService.create(created);
    }

    @Test
    @DisplayName("WorkOrderNoteService - Deve adicionar uma nota formatada corretamente à OS e persistir no banco")
    void shouldAddFormmatedNoteAndPersist() {
        String message = "Serviço iniciado";
        String author = "João";
        service.addNote(workOrder, message, author);

        List<WorkOrderNote> notes = repo.findAll();
        Assertions.assertEquals(2, notes.size());

        WorkOrderNote saved = notes.get(1);
        Assertions.assertEquals("João", saved.getAuthor());
        Assertions.assertEquals(workOrder, saved.getWorkOrder());
        Assertions.assertNotNull(saved.getMessage());
        Assertions.assertTrue(saved.getMessage().contains("Serviço iniciado"));
        Assertions.assertTrue(saved.getMessage().contains("[SYSTEM]") || saved.getMessage().contains("[João]"));
    }

    @Test
    @DisplayName("WorkOrderNoteService - Deve adicionar nota do sistema")
    void shouldAddSystemNote() {
        String message = "OS criada automaticamente";
        String author = "SYSTEM";
        service.addNote(workOrder, message, author);
        List<WorkOrderNote> notes = repo.findAll();
        assertEquals(2, notes.size());
        assertTrue(notes.get(1).getMessage().contains("[SYSTEM]"));
    }

    @Test
    @DisplayName("WorkOrderNoteService - Deve adicionar múltiplas notas na mesma OS")
    void shouldAddMultipleNotes() {
        service.addNote(workOrder, "OS criada", "Maria");
        service.addNote(workOrder, "Técnico atribuído", "Carlos");
        service.addNote(workOrder, "Execução iniciada", "Técnico 1");

        List<WorkOrderNote> notes = repo.findAll();

        assertEquals(4, notes.size());
        assertTrue(notes.stream().anyMatch(n -> n.getMessage().contains("OS criada")));
        assertTrue(notes.stream().anyMatch(n -> n.getMessage().contains("Técnico atribuído")));
        assertTrue(notes.stream().anyMatch(n -> n.getMessage().contains("Execução iniciada")));
    }

}