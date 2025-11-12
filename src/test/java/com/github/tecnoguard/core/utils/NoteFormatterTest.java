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
    @DisplayName("NoteFormatter - Deve formatar mensagem com data, hora e autor")
    void shouldFormatMessage() {
        String msg = "OS criada";
        String author = "João";

        String result = formatter.format(msg, author);

        assertTrue(result.contains("João"));
        assertTrue(result.contains("OS criada"));
        assertTrue(result.matches("\\[\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}\\] \\[João\\] - OS criada"));
    }

    @Test
    @DisplayName("NoteFormatter - Deve usar SYSTEM como autor quando nulo")
    void shouldUseSystemAsDefaultAuthor() {
        String msg = "Mensagem genérica";
        String result = formatter.format(msg, null);
        Assertions.assertTrue(result.contains("[SYSTEM]"));
    }

    @Test
    @DisplayName("NoteFormatter - Deve gerar mensagem de criação corretamente")
    void shouldGenerateCreatedMessage() {
        String result = formatter.created("Carlos");
        assertTrue(result.contains("OS criada por Carlos"));
    }

    @Test
    @DisplayName("NoteFormatter - Deve gerar mensagem de agendamento corretamente")
    void shouldGenerateAssignedMessage() {
        LocalDate date = LocalDate.of(2025, 10, 15);
        String result = formatter.assigned("José", date, "João");
        assertTrue(result.contains("OS agendada para o técnico José no dia 15/10/2025 por João"));
    }

    @Test
    @DisplayName("NoteFormatter - Deve gerar mensagem de iniciação corretamente")
    void shouldGenerateStartedMessage() {
        String result = formatter.started("José");
        assertTrue(result.contains("OS iniciada por José"));
    }

    @Test
    @DisplayName("NoteFormatter - Deve gerar mensagem de conclusão corretamente")
    void shouldGenerateCompletedMessage() {
        LocalDateTime date = LocalDateTime.of(2025, 10, 15, 14, 30);
        String result = formatter.completed("Tudo ok", date, "José");

        assertTrue(result.contains("Conclusão de José [15/10/2025 14:30] : Tudo ok"));
    }

    @Test
    @DisplayName("NoteFormatter - Deve gerar mensagem de cancelamento corretamente")
    void shouldGenerateCancelledMessage() {
        String result = formatter.cancelled("Falta de peças", "Carlos");
        assertTrue(result.contains("OS cancelada por Carlos - Motivo: Falta de peças"));
    }
}