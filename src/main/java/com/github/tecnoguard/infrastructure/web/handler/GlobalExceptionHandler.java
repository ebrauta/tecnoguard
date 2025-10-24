package com.github.tecnoguard.infrastructure.web.handler;

import com.github.tecnoguard.core.exceptions.BusinessException;
import com.github.tecnoguard.core.exceptions.NotFoundException;
import com.github.tecnoguard.core.shared.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        "Regra de negócio inválida",
                        ex.getMessage(),
                        req.getRequestURI()
                )
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        "Não encontrado!",
                        ex.getMessage(),
                        req.getRequestURI()
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        "Erro de validação",
                        ex.getMessage(),
                        req.getRequestURI()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        "Erro interno",
                        ex.getMessage(),
                        req.getRequestURI()
                )
        );
    }

}
