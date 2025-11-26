package com.github.tecnoguard.infrastructure.web.controller;

import com.github.tecnoguard.application.dtos.auth.request.LoginDTO;
import com.github.tecnoguard.application.dtos.auth.response.LoginResponseDTO;
import com.github.tecnoguard.application.dtos.auth.response.UserInfoDTO;
import com.github.tecnoguard.core.dto.ErrorResponseDTO;
import com.github.tecnoguard.core.exceptions.AccessDeniedBusiness;
import com.github.tecnoguard.core.exceptions.NotFoundException;
import com.github.tecnoguard.core.exceptions.WrongLoginException;
import com.github.tecnoguard.domain.models.User;
import com.github.tecnoguard.infrastructure.persistence.UserRepository;
import com.github.tecnoguard.infrastructure.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.Optional;

@Tag(name = "Auth - Autenticação", description = "Gestão de Autenticação e Autorização")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager manager;
    private final UserRepository repo;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager manager, UserRepository repo, TokenService tokenService) {
        this.manager = manager;
        this.repo = repo;
        this.tokenService = tokenService;
    }

    @Operation(
            summary = "Login",
            description = "Autentica usuário.",
            security = @SecurityRequirement(name = "")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Autenticação realizada com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário ou Senha inválidos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Erro de Autenticação",
                                            summary = "Usuário não Autenticado",
                                            value = """
                                                    {
                                                      "timestamp": "2025-11-19T15:00:00.00000000",
                                                      "error": "Login",
                                                      "message": "Usuário ou Senha inválidos",
                                                      "path": "/api/auth/login"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO dto) throws LoginException {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
            Authentication auth = manager.authenticate(usernamePassword);
            SecurityContextHolder.getContext().setAuthentication(auth);
            Optional<User> user = repo.findByUsername(dto.username());
            if (user.isPresent()) {
                user.get().setLastLogin(LocalDateTime.now());
                repo.save(user.get());
            }
            String token = tokenService.generateToken((User) auth.getPrincipal());
            LoginResponseDTO response = new LoginResponseDTO(dto.username(), "Autenticação realizada com sucesso", token, LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new WrongLoginException("Usuário ou Senha inválidos");
        }
    }

    @Operation(
            summary = "Sobre mim",
            description = "Detalha informações do usuário autenticado.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserInfoDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    )
            }
    )
    @GetMapping("/whoami")
    public ResponseEntity<UserInfoDTO> about() throws AccessDeniedBusiness {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedBusiness("Erro de autenticação");
        }
        User authUser = repo.findByUsername(auth.getName()).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        UserInfoDTO response = new UserInfoDTO(authUser.getName(), authUser.getUsername(), auth.getAuthorities().toString());
        return ResponseEntity.ok(response);
    }
}
