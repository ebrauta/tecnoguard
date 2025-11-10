package com.github.tecnoguard.core.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class NoteFormatter {
    public String format(String msg, String user) {
        return String.format("[%s] [%s] - %s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                (user != null) ? user : "SYSTEM",
                msg
        );
    }

    public String created(String author){
        String message = String.format("OS criada por %s", author);
        return format(message, null);
    }

    public String assigned(String technician, LocalDate date, String author){
        String formatedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String message = String.format("OS agendada para o técnico %s no dia %s por %s", technician, formatedDate, author);
        return format(message, null);
    }

    public String started(String author){
        String message = String.format("OS iniciada por %s", author);
        return format(message, null);
    }

    public String completed(String summary, LocalDateTime completedAt, String author){
        String formatedDate = completedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String message = String.format("Conclusão de %s [%s] : %s", author, formatedDate, summary);
        return format(message, null);
    }

    public String cancelled(String reason, String author){
        String message = String.format("OS cancelada por %s - Motivo: %s", author, reason);
        return format(message, null);
    }
}
