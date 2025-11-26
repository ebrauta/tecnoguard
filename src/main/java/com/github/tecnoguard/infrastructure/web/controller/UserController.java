package com.github.tecnoguard.infrastructure.web.controller;

import com.github.tecnoguard.application.dtos.user.request.ChangePasswordDTO;
import com.github.tecnoguard.application.dtos.user.request.CreateUserDTO;
import com.github.tecnoguard.application.dtos.user.request.UpdateUserDTO;
import com.github.tecnoguard.application.dtos.user.response.UserResponseDTO;
import com.github.tecnoguard.application.mappers.users.UserMapper;
import com.github.tecnoguard.core.dto.ErrorResponseDTO;
import com.github.tecnoguard.core.dto.PageDTO;
import com.github.tecnoguard.domain.models.User;
import com.github.tecnoguard.infrastructure.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User - Usuários", description = "Gestão de Usuários")
@RestController
@RequestMapping("/api/users")
public class UserController {

    public static class UserPageDTO extends PageDTO<UserResponseDTO> {
    }

    private final UserServiceImpl service;
    private final UserMapper mapper;

    public UserController(UserServiceImpl service, UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(
            summary = "Registrar usuário",
            description = "Cadastra o usuário.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Usuário criado com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody CreateUserDTO dto) {
        User user = mapper.toEntity(dto);
        User created = service.create(user);
        UserResponseDTO response = mapper.toResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Listar todos",
            description = "Lista todos os usuários.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserPageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    )
            }
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<PageDTO<UserResponseDTO>> list(
            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<UserResponseDTO> page = service.list(pageable).map(mapper::toResponse);
        return ResponseEntity.status(HttpStatus.OK).body(new PageDTO<>(page));
    }

    @Operation(
            summary = "Detalhes",
            description = "Mostra informação do usuário.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuário Não Encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "404", ref = "404")
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<UserResponseDTO> get(@PathVariable Long id) {
        User user = service.findById(id);
        UserResponseDTO response = mapper.toResponse(user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Atualizar",
            description = "Atualiza informação do usuário.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuário atualizado com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuário Não Encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "404", ref = "404")
                            )
                    )
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO dto) {
        User user = service.findById(id);
        mapper.updateEntity(user, dto);
        User updated = service.update(id, user);
        UserResponseDTO response = mapper.toResponse(updated);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Desativar",
            description = "Desativa usuário.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Desativado com sucesso"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuário Não Encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "404", ref = "404")
                            )
                    )
            }
    )
    @DeleteMapping("/deactivate/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Reativar",
            description = "Reativa usuário.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Reativado com sucesso"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuário Não Encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "404", ref = "404")
                            )
                    )
            }
    )
    @PatchMapping("/reactivate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        service.reactivate(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Mudar senha",
            description = "Altera a senha do usuário.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Senha alterada com sucesso"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuário Não Encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "404", ref = "404")
                            )
                    )
            }
    )
    @PatchMapping("/password/{id}")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordDTO dto) {
        service.changePassword(id, dto.currentPassword(), dto.newPassword());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
