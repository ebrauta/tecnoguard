package com.github.tecnoguard.infrastructure.web.controller;

import com.github.tecnoguard.application.dtos.user.request.LoginDTO;
import com.github.tecnoguard.application.dtos.user.request.RegisterUserDTO;
import com.github.tecnoguard.application.dtos.user.response.LoginResponseDTO;
import com.github.tecnoguard.application.dtos.user.response.RegisterReponseDTO;
import com.github.tecnoguard.application.dtos.user.response.UserInfoDTO;
import com.github.tecnoguard.application.mappers.users.UserMapper;
import com.github.tecnoguard.core.shared.ErrorResponse;
import com.github.tecnoguard.domain.models.User;
import com.github.tecnoguard.infrastructure.persistence.UserRepository;
import com.github.tecnoguard.infrastructure.security.TokenService;
import com.github.tecnoguard.infrastructure.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Auth - Autenticação", description = "Gestão de Autenticação e Autorização")
@RestController
@RequestMapping("/api/auth")
public class AuthContoller {

    private final AuthenticationManager manager;
    private final PasswordEncoder encoder;
    private final UserRepository repo;
    private final UserServiceImpl service;
    private final UserMapper mapper = new UserMapper();
    private final TokenService tokenService;

    public AuthContoller(AuthenticationManager manager,
                         PasswordEncoder encoder,
                         TokenService tokenService,
                         UserRepository repo,
                         UserServiceImpl service) {
        this.manager = manager;
        this.encoder = encoder;
        this.tokenService = tokenService;
        this.repo = repo;
        this.service = service;
    }

    @Operation(summary = "Registrar", description = "Cadastra o usuário.")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDTO dto) {
        if (repo.findByUsername(dto.username()).isPresent()) {
            ErrorResponse eResponse = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.name(),
                    "Usuário já existe",
                    "/register"
            );
            return ResponseEntity.badRequest().body(eResponse);
        }

        User user = mapper.fromRegisterToEntity(dto);
        user = service.create(user);

        RegisterReponseDTO response = mapper.fromUserToResponse(user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login", description = "Autentica usuário.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
            Authentication auth = manager.authenticate(usernamePassword);
            SecurityContextHolder.getContext().setAuthentication(auth);
            String token = tokenService.generateToken((User) auth.getPrincipal());
            LoginResponseDTO response = new LoginResponseDTO(
                    dto.username(),
                    "Autenticação realizada com sucesso",
                    token,
                    LocalDateTime.now()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ErrorResponse eResponse = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.UNAUTHORIZED.name(),
                    "Usuário ou Senha inválidos",
                    "/login"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(eResponse);
        }
    }

    @Operation(summary = "About me", description = "Detalha informações do usuário autenticado.")
    @GetMapping("/aboutme")
    public ResponseEntity<?> about() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            ErrorResponse eResponse = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.UNAUTHORIZED.name(),
                    "Usuário não autenticado",
                    "/aboutme"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(eResponse);
        }
        return ResponseEntity.ok(new UserInfoDTO(
                auth.getName(),
                auth.getAuthorities().toString()
        ));
    }

}
