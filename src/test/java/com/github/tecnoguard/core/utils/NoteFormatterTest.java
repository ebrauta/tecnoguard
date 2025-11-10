package com.github.tecnoguard.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NoteFormatterTest {

    private NoteFormatter formatter;

    @BeforeEach
    void setUp(){
        formatter = new NoteFormatter();
    }

    @Test
    @DisplayName("Deve formatar mensagem com data, hora e autor")
    void shouldFormatMessage() {
        String msg = "OS criada";
        String author = "João";

        String result = formatter.format(msg, author);

        assertTrue(result.contains("João"));
        assertTrue(result.contains("OS criada"));
        assertTrue(result.matches("\\[\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}\\] \\[João\\] - OS criada"));
    }

    @Test
    @DisplayName("Deve usar SYSTEM como autor quando nulo")
    void shouldUseSystemAsDefaultAuthor() {
        String msg = "Mensagem genérica";
        String result = formatter.format(msg, null);
        Assertions.assertTrue(result.contains("[SYSTEM]"));
    }

    @Test
    @DisplayName("Deve gerar mensagem de criação corretamente")
    void shouldGenerateCreatedMessage() {
        String result = formatter.created("Carlos");
        assertTrue(result.contains("OS criada por Carlos"));
        assertTrue(result.contains("[SYSTEM]"));
    }

    @Test
    @DisplayName("Deve gerar mensagem de agendamento corretamente")
    void shouldGenerateAssignedMessage() {
        LocalDate date = LocalDate.of(2025, 10, 15);
        String result = formatter.assigned("Técnico 1", date, "João");
        assertTrue(result.contains("OS agendada para o técnico Técnico 1"));
        assertTrue(result.contains("15/10/2025"));
    }

    @Test
    @DisplayName("Deve gerar mensagem de conclusão corretamente")
    void shouldGenerateCompletedMessage() {
        LocalDateTime date = LocalDateTime.of(2025, 10, 15, 14, 30);
        String result = formatter.completed("Tudo ok", date, "Ana");

        assertTrue(result.contains("Conclusão de Ana"));
        assertTrue(result.contains("Tudo ok"));
        assertTrue(result.contains("15/10/2025"));
    }

    @Test
    @DisplayName("Deve gerar mensagem de cancelamento corretamente")
    void shouldGenerateCancelledMessage() {
        String result = formatter.cancelled("Falta de peças", "Carlos");
        assertTrue(result.contains("OS cancelada por Carlos"));
        assertTrue(result.contains("Motivo: Falta de peças"));
    }
}