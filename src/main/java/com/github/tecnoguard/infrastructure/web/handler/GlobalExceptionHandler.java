package com.github.tecnoguard.infrastructure.web.handler;

import com.github.tecnoguard.core.exceptions.*;
import com.github.tecnoguard.core.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusiness(BusinessException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseDTO(
                        LocalDateTime.now(),
                        "Regra de negócio inválida",
                        ex.getMessage(),
                        req.getRequestURI()
                )
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseDTO(
                        LocalDateTime.now(),
                        "Não encontrado!",
                        ex.getMessage(),
                        req.getRequestURI()
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseDTO(
                        LocalDateTime.now(),
                        "Erro de validação",
                        ex.getMessage(),
                        req.getRequestURI()
                ));
    }

    @ExceptionHandler(WrongLoginException.class)
    public ResponseEntity<ErrorResponseDTO> handleLogin(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponseDTO(
                        LocalDateTime.now(),
                        "Erro de Login",
                        ex.getMessage(),
                        req.getRequestURI()
                )
        );
    }

    @ExceptionHandler(AccessDeniedBusiness.class)
    public ResponseEntity<ErrorResponseDTO> handleAboutMe(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponseDTO(
                        LocalDateTime.now(),
                        "Usuário não autenticado",
                        ex.getMessage(),
                        req.getRequestURI()
                )
        );
    }

    @ExceptionHandler(DuplicatedException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicated(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseDTO(
                        LocalDateTime.now(),
                        "Registro duplicado",
                        ex.getMessage(),
                        req.getRequestURI()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseDTO(
                        LocalDateTime.now(),
                        "Erro interno",
                        ex.getMessage(),
                        req.getRequestURI()
                )
        );
    }

}
