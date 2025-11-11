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
        return String.format("OS criada por %s", author);
    }

    public String assigned(String technician, LocalDate date, String author){
        String formatedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return String.format("OS agendada para o técnico %s no dia %s por %s", technician, formatedDate, author);
    }

    public String started(String author){
        return String.format("OS iniciada por %s", author);
    }

    public String completed(String summary, LocalDateTime completedAt, String author){
        String formatedDate = completedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return String.format("Conclusão de %s [%s] : %s", author, formatedDate, summary);
    }

    public String cancelled(String reason, String author){
        return String.format("OS cancelada por %s - Motivo: %s", author, reason);
    }
}
