package com.github.tecnoguard.infrastructure.web.controller;

import com.github.tecnoguard.application.dtos.user.request.LoginDTO;
import com.github.tecnoguard.application.dtos.user.request.RegisterUserDTO;
import com.github.tecnoguard.application.dtos.user.response.LoginResponseDTO;
import com.github.tecnoguard.application.dtos.user.response.RegisterReponseDTO;
import com.github.tecnoguard.application.dtos.user.response.UserInfoDTO;
import com.github.tecnoguard.application.mappers.users.UserMapper;
import com.github.tecnoguard.domain.models.User;
import com.github.tecnoguard.infrastructure.persistence.UserRepository;
import com.github.tecnoguard.infrastructure.service.UserServiceImpl;
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

@RestController
@RequestMapping("/api/auth")
public class AuthContoller {

    private final AuthenticationManager manager;
    private final PasswordEncoder encoder;
    private final UserRepository repo;
    private final UserServiceImpl service;
    private final UserMapper mapper = new UserMapper();

    public AuthContoller(AuthenticationManager manager,
                         PasswordEncoder encoder,
                         UserRepository repo,
                         UserServiceImpl service) {
        this.manager = manager;
        this.encoder = encoder;
        this.repo = repo;
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDTO dto) {
        if (repo.findByUsername(dto.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Usuário já existe");
        }
        /*User user = new User();
        user.setUsername(dto.username());
        user.setPassword(encoder.encode(dto.password()));
        user.setRole(dto.role() != null ? dto.role() : UserRole.OPERATOR);

        repo.save(user);
        */

        User user = mapper.fromRegisterToEntity(dto);
        user = service.create(user);

       /* RegisterReponseDTO response = new RegisterReponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getCreatedAt());*/

        RegisterReponseDTO response = mapper.fromUserToResponse(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        try {
            Authentication auth = manager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            LoginResponseDTO response = new LoginResponseDTO(
                    dto.username(),
                    "Autenticação realizada com sucesso",
                    LocalDateTime.now()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ou senha inválidos");
        }
    }

    @GetMapping("/aboutme")
    public ResponseEntity<?> about() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não autenticado");
        }
        return ResponseEntity.ok(new UserInfoDTO(
                auth.getName(),
                auth.getAuthorities().toString()
        ));
    }

}
